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
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.mobile.app_iara.R
import com.mobile.app_iara.data.repository.AbacusPhotoRepository
import com.mobile.app_iara.ui.abacus.confirmation.AbacusConfirmationActivity
import com.mobile.app_iara.ui.status.LoadingFragment
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
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.FileOutputStream
import kotlinx.coroutines.launch

class CameraActivity: AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var captureButton: ImageButton
    private lateinit var flashButton: ImageButton
    private lateinit var addFileButton: ImageButton
    private lateinit var tvDetectionLabel: TextView
    private var loadingFragment: LoadingFragment? = null


    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { imageUri ->
            showLoadingFragment()

            lifecycleScope.launch {
                val imageFile = uriToFile(imageUri)

                if (imageFile == null) {
                    hideLoadingFragment()
                    Toast.makeText(this@CameraActivity, "Erro ao carregar imagem da galeria", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val result = abacusPhotoRepository.analyzeAbacusAndGetCsv(
                    imageFile = imageFile,
                    colors = abacusColors,
                    values = abacusValues
                )

                hideLoadingFragment()

                result.onSuccess { csvData ->
                    Log.d("CameraActivity", "CSV (Galeria) Recebido: $csvData")

                    val intent = Intent(this@CameraActivity, AbacusConfirmationActivity::class.java)
                    intent.putExtra("image_uri", imageUri.toString())
                    intent.putExtra("csv_data", csvData)
                    intent.putExtra("ABACUS_ID", abacusId)
                    intent.putExtra("FACTORY_ID", factoryId)
                    startActivity(intent)

                    finish()
                }

                result.onFailure { error ->
                    Log.e("CameraActivity", "Erro na análise (Galeria): ${error.message}", error)
                    Toast.makeText(this@CameraActivity, "Erro na análise: ${error.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private var imageCapture: ImageCapture? = null
    private var imageAnalysis: ImageAnalysis? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private lateinit var cameraExecutor: ExecutorService
    private var flashEnabled = false
    private var tfliteInterpreter: Interpreter? = null
    private val inputSize = 224
    private var lastProcessTime = 0L
    private val processInterval = 300L
    private val isDestroyed = AtomicBoolean(false)
    private val isProcessing = AtomicBoolean(false)
    private val abacusPhotoRepository = AbacusPhotoRepository()
    private var abacusColors: String? = null
    private var abacusValues: String? = null
    private var abacusId: String? = null
    private var factoryId: Int = -1

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted && !isDestroyed.get()) startCamera()
            else Toast.makeText(this, "Permissão da câmera negada", Toast.LENGTH_SHORT).show()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_camera)

        previewView = findViewById(R.id.previewView)
        captureButton = findViewById(R.id.btnCapture)
        flashButton = findViewById(R.id.btnFlash)
        addFileButton = findViewById(R.id.btnAddFile)
        tvDetectionLabel = findViewById(R.id.tvDetectionLabel)
        abacusColors = intent.getStringExtra("ABACUS_COLORS")
        abacusValues = intent.getStringExtra("ABACUS_VALUES")
        abacusId = intent.getStringExtra("ABACUS_ID")
        factoryId = intent.getIntExtra("FACTORY_ID", -1)


        initializeTFLite()

        if (hasFlashlight()) {
            flashButton.isEnabled = true
            flashButton.setOnClickListener { toggleFlash() }
        } else {
            flashButton.isEnabled = false
            flashButton.setOnClickListener(null)
        }

        updateFlashButtonUI()
        updateDetectionLabel(false, 0f)

        cameraExecutor = Executors.newSingleThreadExecutor { r ->
            Thread(r, "CameraExecutor").apply {
                isDaemon = true
            }
        }

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
        if (isDestroyed.get()) return

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            try {
                if (isDestroyed.get()) return@addListener

                cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                imageCapture = ImageCapture.Builder()
                    .setFlashMode(ImageCapture.FLASH_MODE_OFF)
                    .build()

                imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analysis ->
                        analysis.setAnalyzer(cameraExecutor) { image ->
                            safeClassifyImage(image)
                        }
                    }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalysis
                )
            } catch (exc: Exception) {
                Log.e("CameraOverlay", "Erro ao iniciar a câmera", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun safeClassifyImage(image: ImageProxy) {
        if (isDestroyed.get() ||
            tfliteInterpreter == null ||
            !isProcessing.compareAndSet(false, true)) {
            image.close()
            return
        }

        try {
            classifyImage(image)
        } catch (e: Exception) {
            Log.e("CameraOverlay", "Erro na classificação segura: ${e.message}", e)
        } finally {
            isProcessing.set(false)
        }
    }

    private fun classifyImage(image: ImageProxy) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastProcessTime < processInterval) {
            image.close()
            return
        }
        lastProcessTime = currentTime

        try {
            if (isDestroyed.get() || tfliteInterpreter == null) {
                return
            }

            Log.d("TFLite", "Processando imagem - Formato: ${image.format}, Tamanho: ${image.width}x${image.height}")

            val bitmap = imageProxyToBitmap(image)
            if (bitmap == null) {
                Log.w("TFLite", "Falha ao converter ImageProxy para Bitmap")
                return
            }

            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, false)
            val inputBuffer = preprocessImage(resizedBitmap)

            if (isDestroyed.get() || tfliteInterpreter == null) {
                return
            }

            val output = Array(1) { FloatArray(2) }
            tfliteInterpreter?.run(inputBuffer, output)

            val probs = softmax(output[0])
            val abacusConfidence = probs[0] * 100

            Log.d("TFLite", "Classificação - Ábaco: ${String.format("%.1f", abacusConfidence)}%")

            val isAbacusDetected = abacusConfidence > 60

            if (!isDestroyed.get()) {
                runOnUiThread {
                    if (!isDestroyed.get()) {
                        updateDetectionLabel(isAbacusDetected, abacusConfidence)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("TFLite", "Erro na classificação: ${e.message}", e)
        } finally {
            try {
                image.close()
            } catch (e: Exception) {
                Log.w("TFLite", "Erro ao fechar imagem: ${e.message}")
            }
        }
    }

    private fun updateDetectionLabel(isDetected: Boolean, confidence: Float) {
        if (isDestroyed.get()) return

        try {
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
                        ContextCompat.getColor(this@CameraActivity, android.R.color.holo_green_dark)
                    else
                        ContextCompat.getColor(this@CameraActivity, android.R.color.holo_red_dark)
                )
            }
            tvDetectionLabel.background = drawable
        } catch (e: Exception) {
            Log.e("CameraOverlay", "Erro ao atualizar label: ${e.message}")
        }
    }

    private fun imageProxyToBitmap(image: ImageProxy): Bitmap? {
        return try {
            when (image.format) {
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
        } catch (e: Exception) {
            Log.e("CameraOverlay", "Erro na conversão de imagem: ${e.message}")
            createEmptyBitmap()
        }
    }

    private fun yuvToRgbBitmap(image: ImageProxy): Bitmap? {
        return try {
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
            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        } catch (e: Exception) {
            Log.e("CameraOverlay", "Erro na conversão YUV: ${e.message}")
            createEmptyBitmap()
        }
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
                    Log.d("CameraActivity", "Foto salva: $savedUri")

                    showLoadingFragment()

                    lifecycleScope.launch {
                        val result = abacusPhotoRepository.analyzeAbacusAndGetCsv(
                            imageFile = photoFile,
                            colors = abacusColors,
                            values = abacusValues
                        )

                        hideLoadingFragment()

                        result.onSuccess { csvData ->
                            Log.d("CameraActivity", "CSV Recebido: $csvData")

                            val intent = Intent(this@CameraActivity, AbacusConfirmationActivity::class.java)
                            intent.putExtra("image_uri", savedUri.toString())
                            intent.putExtra("csv_data", csvData)
                            intent.putExtra("ABACUS_ID", abacusId)
                            intent.putExtra("FACTORY_ID", factoryId)
                            startActivity(intent)

                            finish()
                        }

                        result.onFailure { error ->
                            Log.e("CameraActivity", "Erro na análise: ${error.message}", error)
                            Toast.makeText(this@CameraActivity, "Erro na análise: ${error.message}", Toast.LENGTH_LONG).show()
                        }
                    }
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
        isDestroyed.set(true)
        loadingFragment?.stopLoading()
        loadingFragment = null

        try {
            imageAnalysis?.clearAnalyzer()

            cameraProvider?.unbindAll()

            if (!cameraExecutor.isShutdown) {
                cameraExecutor.shutdown()
                try {
                    if (!cameraExecutor.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                        cameraExecutor.shutdownNow()
                    }
                } catch (e: InterruptedException) {
                    cameraExecutor.shutdownNow()
                    Thread.currentThread().interrupt()
                }
            }

            tfliteInterpreter?.close()
            tfliteInterpreter = null

        } catch (e: Exception) {
            Log.e("CameraOverlay", "Erro no onDestroy: ${e.message}", e)
        } finally {
            super.onDestroy()
        }
    }

    private fun uriToFile(uri: Uri): File? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val tempFile = File(cacheDir, "temp_gallery_image.jpg")
            val outputStream = FileOutputStream(tempFile)

            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            tempFile
        } catch (e: Exception) {
            Log.e("CameraActivity", "Erro ao converter Uri para File", e)
            null
        }
    }

    private fun showLoadingFragment() {
        if (loadingFragment == null) {
            loadingFragment = LoadingFragment.newInstance()
        }

        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, loadingFragment!!)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    private fun hideLoadingFragment() {
        loadingFragment?.let {
            supportFragmentManager.beginTransaction()
                .remove(it)
                .commitAllowingStateLoss()
            loadingFragment = null
        }
    }

    override fun onPause() {
        super.onPause()
        imageAnalysis?.clearAnalyzer()
    }

    override fun onResume() {
        super.onResume()
        if (!isDestroyed.get() && imageAnalysis != null && tfliteInterpreter != null) {
            imageAnalysis?.setAnalyzer(cameraExecutor) { image ->
                safeClassifyImage(image)
            }
        }
    }
}