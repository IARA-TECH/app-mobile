package com.mobile.app_iara.ui.camera

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.google.mlkit.vision.objects.DetectedObject

class GraphicOverlay(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var results: List<DetectedObject> = emptyList()
    private var imageWidth: Int = 0
    private var imageHeight: Int = 0

    private val paint = Paint().apply {
        color = Color.WHITE          // borda branca
        style = Paint.Style.STROKE
        strokeWidth = 8f             // um pouco mais grossa
        isAntiAlias = true
        setShadowLayer(12f, 0f, 0f, Color.BLACK) // leve sombra para destacar
    }


    // Animadores por objeto (trackingId)
    private val animators = mutableMapOf<Int, BoundingBoxAnimator>()

    fun setResults(results: List<DetectedObject>, imageWidth: Int, imageHeight: Int) {
        val filtered = results.filter { obj ->
            val hasLabel = obj.labels.any { it.confidence > 0.6f }
            val bigEnough = obj.boundingBox.width() * obj.boundingBox.height() > 40000
            hasLabel && bigEnough
        }

        this.results = filtered
        this.imageWidth = imageWidth
        this.imageHeight = imageHeight

        for (obj in filtered) {
            val id = obj.trackingId ?: obj.hashCode()
            val rect = translateRect(obj.boundingBox, imageWidth, imageHeight, width, height)

            if (!animators.containsKey(id)) {
                animators[id] = BoundingBoxAnimator(this, rect).apply { start() }
            } else {
                animators[id]?.updateTarget(rect)
            }
        }

        val idsAtuais = filtered.map { it.trackingId ?: it.hashCode() }
        animators.keys.retainAll(idsAtuais)

        postInvalidate()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        animators.values.forEach { animator ->
            paint.alpha = (animator.alphaScale * 255).toInt()
            val radius = 50f // cantos mais arredondados
            canvas.drawRoundRect(animator.animatedBox, radius, radius, paint)
        }
    }

    private fun translateRect(
        rect: Rect,
        imageWidth: Int,
        imageHeight: Int,
        viewWidth: Int,
        viewHeight: Int
    ): RectF {
        val scaleX = viewWidth.toFloat() / imageWidth.toFloat()
        val scaleY = viewHeight.toFloat() / imageHeight.toFloat()
        val scale = scaleX.coerceAtLeast(scaleY)

        val offsetX = (viewWidth - imageWidth * scale) / 2
        val offsetY = (viewHeight - imageHeight * scale) / 2

        return RectF(
            rect.left * scale + offsetX,
            rect.top * scale + offsetY,
            rect.right * scale + offsetX,
            rect.bottom * scale + offsetY
        )
    }
}
