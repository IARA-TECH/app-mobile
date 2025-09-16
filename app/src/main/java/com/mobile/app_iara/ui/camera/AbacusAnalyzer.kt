package com.mobile.app_iara.ui.camera

import android.util.Log
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions
import com.google.mlkit.common.model.LocalModel

class AbacusAnalyzer(
    private val graphicOverlay: GraphicOverlay
) : ImageAnalysis.Analyzer {

    // 🔹 Carrega o modelo local (o que você exportou do Teachable Machine)
    private val localModel = LocalModel.Builder()
        .setAssetFilePath("model.tflite")
        .build()

    private val options = CustomObjectDetectorOptions.Builder(localModel)
        .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
        .enableClassification()
        .build()

    private val objectDetector = ObjectDetection.getClient(options)

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: run {
            imageProxy.close()
            return
        }

        val rotation = imageProxy.imageInfo.rotationDegrees
        val inputImage = InputImage.fromMediaImage(mediaImage, rotation)

        objectDetector.process(inputImage)
            .addOnSuccessListener { results: List<DetectedObject> ->
                for (obj in results) {
                    if (obj.labels.isNotEmpty()) {
                        val topLabel = obj.labels.first()
                        val labelText = topLabel.text
                        val confidence = String.format("%.2f", topLabel.confidence * 100)

                        Toast.makeText(
                            graphicOverlay.context,
                            "Detectado: $labelText ($confidence%)",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                graphicOverlay.setResults(results, inputImage.width, inputImage.height)
            }
            .addOnFailureListener { e ->
                Log.w("AbacusAnalyzer", "Detect failed: ${e.localizedMessage}")
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
}
