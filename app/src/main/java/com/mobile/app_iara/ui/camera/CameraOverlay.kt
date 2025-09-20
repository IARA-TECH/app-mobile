package com.mobile.app_iara.ui.camera

import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.Intent
import com.mobile.app_iara.R
import org.tensorflow.lite.Interpreter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.FileOutputStream

class CameraOverlay : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var captureButton: ImageButton
    private lateinit var flashButton: ImageButton
    private lateinit var addFileButton: ImageButton
    private lateinit var tvDetectionLabel: TextView

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            redirectToNextScreen(it)
        }
    }

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private var flashEnabled = false

    private var tfliteInterpreter: Interpreter? = null
    private val inputSize = 224
    private var lastProcessTime = 0L
    private val processInterval = 300L

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) startCamera()
            else Toast.makeText(this, "Permissão da câmera negada", Toast.LENGTH_SHORT).show()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_overlay)

        previewView = findViewById(R.id.previewView)
        captureButton = findViewById(R.id.btnCapture)
        flashButton = findViewById(R.id.btnFlash)
        addFileButton = findViewById(R.id.btnAddFile)
        tvDetectionLabel = findViewById(R.id.tvDetectionLabel)
        val backButton = findViewById<ImageButton>(R.id.btnVoltar)

        initializeTFLite()

        if (hasFlashlight()) {
            flashButton.isEnabled = true
            flashButton.setOnClickListener { toggleFlash() }
        } else {
            flashButton.isEnabled = false
            flashButton.setOnClickListener(null)
        }

        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        updateFlashButtonUI()
        updateDetectionLabel(false, 0f)

        cameraExecutor = Executors.newSingleThreadExecutor()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        captureButton.setOnClickListener { takePhoto() }
        flashButton.setOnClickListener { toggleFlash() }
        addFileButton.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
    }

    private fun initializeTFLite() {
        try {
            val tfliteModel = loadModelFile("abacus_classifier.tflite")
            val options = Interpreter.Options()
            tfliteInterpreter = Interpreter(tfliteModel, options)
            Log.d("TFLite", "Modelo carregado com sucesso")
        } catch (e: Exception) {
            Log.e("TFLite", "Erro ao carregar o modelo: ${e.message}")
        }
    }

    private fun loadModelFile(modelPath: String): MappedByteBuffer {
        val fileDescriptor = assets.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder()
                .setFlashMode(ImageCapture.FLASH_MODE_OFF)
                .build()

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, ImageAnalysis.Analyzer { image ->
                        classifyImage(image)
                    })
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer
                )
            } catch (exc: Exception) {
                Log.e("CameraOverlay", "Erro ao iniciar a câmera", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun classifyImage(image: ImageProxy) {
        if (tfliteInterpreter == null) {
            image.close()
            return
        }

        val currentTime = System.currentTimeMillis()
        if (currentTime - lastProcessTime < processInterval) {
            image.close()
            return
        }
        lastProcessTime = currentTime

        try {
            Log.d("TFLite", "Processando imagem - Formato: ${image.format}, Tamanho: ${image.width}x${image.height}")

            val bitmap = imageProxyToBitmap(image)
            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, false)
            val inputBuffer = preprocessImage(resizedBitmap)

            val output = Array(1) { FloatArray(2) }
            tfliteInterpreter?.run(inputBuffer, output)

            val probs = softmax(output[0])

            val abacusConfidence = probs[0] * 100

            Log.d("TFLite", "Classificação - Ábaco: ${String.format("%.1f", abacusConfidence)}%")

            val isAbacusDetected = abacusConfidence > 60
            runOnUiThread {
                updateDetectionLabel(isAbacusDetected, abacusConfidence)
            }
        } catch (e: Exception) {
            Log.e("TFLite", "Erro na classificação: ${e.message}")
            e.printStackTrace()
        } finally {
            image.close()
        }
    }

    private fun updateDetectionLabel(isDetected: Boolean, confidence: Float) {
        if (isDetected) {
            tvDetectionLabel.text = "Ábaco detectado"
            tvDetectionLabel.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
        } else {
            tvDetectionLabel.text = "Ábaco não detectado"
            tvDetectionLabel.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
        }

        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 20f * resources.displayMetrics.density
            setColor(
                if (isDetected)
                    ContextCompat.getColor(this@CameraOverlay, android.R.color.holo_green_dark)
                else
                    ContextCompat.getColor(this@CameraOverlay, android.R.color.holo_red_dark)
            )
        }
        tvDetectionLabel.background = drawable
    }

    private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        return when (image.format) {
            ImageFormat.YUV_420_888 -> {
                yuvToRgbBitmap(image)
            }
            ImageFormat.JPEG -> {
                val buffer = image.planes[0].buffer
                val bytes = ByteArray(buffer.remaining())
                buffer.get(bytes)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            }
            else -> {
                val buffer = image.planes[0].buffer
                val bytes = ByteArray(buffer.remaining())
                buffer.get(bytes)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    ?: createEmptyBitmap()
            }
        }
    }

    private fun yuvToRgbBitmap(image: ImageProxy): Bitmap {
        val planes = image.planes
        val yBuffer = planes[0].buffer
        val uBuffer = planes[1].buffer
        val vBuffer = planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, image.width, image.height), 100, out)
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    private fun createEmptyBitmap(): Bitmap {
        return Bitmap.createBitmap(inputSize, inputSize, Bitmap.Config.RGB_565)
    }

    private fun preprocessImage(bitmap: Bitmap): ByteBuffer {
        val inputBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * 3)
        inputBuffer.order(ByteOrder.nativeOrder())

        val pixels = IntArray(inputSize * inputSize)
        bitmap.getPixels(pixels, 0, inputSize, 0, 0, inputSize, inputSize)

        for (pixel in pixels) {
            val r = ((pixel shr 16) and 0xFF) / 255.0f
            val g = ((pixel shr 8) and 0xFF) / 255.0f
            val b = (pixel and 0xFF) / 255.0f

            inputBuffer.putFloat(r)
            inputBuffer.putFloat(g)
            inputBuffer.putFloat(b)
        }

        return inputBuffer
    }

    private fun softmax(logits: FloatArray): FloatArray {
        val expValues = logits.map { Math.exp(it.toDouble()) }
        val sum = expValues.sum()
        return expValues.map { (it / sum).toFloat() }.toFloatArray()
    }

    private fun downloadModelFile(url: String, destination: File, onComplete: (Boolean) -> Unit) {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        Thread {
            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        onComplete(false)
                        return@Thread
                    }

                    val sink = FileOutputStream(destination)
                    sink.use {
                        it.write(response.body?.bytes())
                    }
                    onComplete(true)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onComplete(false)
            }
        }.start()
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = File(
            externalMediaDirs.first(),
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                .format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    redirectToNextScreen(savedUri)
                }

                override fun onError(exc: ImageCaptureException) {
                    Log.e("CameraOverlay", "Erro ao capturar foto: ${exc.message}", exc)
                }
            }
        )
    }

    private fun toggleFlash() {
        flashEnabled = !flashEnabled
        imageCapture?.flashMode =
            if (flashEnabled) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF

        updateFlashButtonUI()
    }

    private fun updateFlashButtonUI() {
        if (flashEnabled) {
            flashButton.setImageResource(R.drawable.ic_flash_on)
        } else {
            flashButton.setImageResource(R.drawable.ic_flash_off)
        }
    }

    private fun redirectToNextScreen(imageUri: Uri) {
        val data = Intent().apply { putExtra("IMAGEM_URI", imageUri.toString()) }
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    private fun hasFlashlight(): Boolean {
        return packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }

    override fun onDestroy() {
        super.onDestroy()
        tfliteInterpreter?.close()
        cameraExecutor.shutdown()
    }
}