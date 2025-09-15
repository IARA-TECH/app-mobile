package com.mobile.app_iara.ui.camera

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.graphics.RectF

class BoundingBoxAnimator(
    private val graphicOverlay: GraphicOverlay,
    private var targetBox: RectF
) {
    var animatedBox: RectF = RectF()
    var alphaScale = 0f
        private set

    private val animatorSet: AnimatorSet

    init {
        val scaleAnimator = ValueAnimator.ofFloat(0f, 1f).setDuration(300)
        scaleAnimator.addUpdateListener { anim ->
            val scale = anim.animatedValue as Float
            val centerX = targetBox.centerX()
            val centerY = targetBox.centerY()
            animatedBox.set(
                centerX - targetBox.width() / 2 * scale,
                centerY - targetBox.height() / 2 * scale,
                centerX + targetBox.width() / 2 * scale,
                centerY + targetBox.height() / 2 * scale
            )
            graphicOverlay.postInvalidate()
        }

        val fadeAnimator = ValueAnimator.ofFloat(0f, 1f).setDuration(250)
        fadeAnimator.addUpdateListener { anim ->
            alphaScale = anim.animatedValue as Float
            graphicOverlay.postInvalidate()
        }

        animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleAnimator, fadeAnimator)
    }

    fun start() {
        if (!animatorSet.isRunning) animatorSet.start()
    }

    fun updateTarget(newBox: RectF) {
        targetBox = newBox
        animatedBox = newBox
        graphicOverlay.postInvalidate()
    }
}
