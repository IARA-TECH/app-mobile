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
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions

class AbacusAnalyzer(
    private val graphicOverlay: GraphicOverlay
) : ImageAnalysis.Analyzer {

    private val options = ObjectDetectorOptions.Builder()
        .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
        .enableMultipleObjects()
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
