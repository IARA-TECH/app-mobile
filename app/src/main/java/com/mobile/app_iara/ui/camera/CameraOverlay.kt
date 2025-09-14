package com.mobile.app_iara.ui.camera

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.Intent
import com.mobile.app_iara.R
import com.mobile.app_iara.ui.home.HomeFragment
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CameraOverlay : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var captureButton: ImageButton
    private lateinit var flashButton: ImageButton
    private lateinit var addFileButton: ImageButton

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            redirectToNextScreen(it)
        }
    }

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private var flashEnabled = false

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
        val backButton = findViewById<ImageButton>(R.id.btnVoltar)


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

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                val camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (exc: Exception) {
                Log.e("CameraOverlay", "Erro ao iniciar a câmera", exc)
            }

        }, ContextCompat.getMainExecutor(this))
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
                    Toast.makeText(baseContext, "Foto salva em: $savedUri", Toast.LENGTH_SHORT).show()
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
        val intent = Intent(this, HomeFragment::class.java).apply {
            putExtra("IMAGEM_URI", imageUri.toString())
        }
        startActivity(intent)
    }

    private fun hasFlashlight(): Boolean {
        return packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}