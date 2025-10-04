package com.mobile.app_iara.ui.dashboard.farmcondemnation

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.HorizontalBarChartRenderer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler

class RoundedHorizontalBarChartRenderer(
    chart: BarDataProvider,
    animator: ChartAnimator,
    viewPortHandler: ViewPortHandler,
    private val labels: List<String>
) : HorizontalBarChartRenderer(chart, animator, viewPortHandler) {

    private val barRadius = 25f
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = Utils.convertDpToPixel(12f)
        textAlign = Paint.Align.LEFT
    }

    override fun drawDataSet(c: Canvas, dataSet: IBarDataSet, index: Int) {
        val trans = mChart.getTransformer(dataSet.axisDependency)
        mBarBorderPaint.color = dataSet.barBorderColor
        mBarBorderPaint.strokeWidth = dataSet.barBorderWidth
        val drawBorder = dataSet.barBorderWidth > 0f
        val phaseX = mAnimator.phaseX
        val phaseY = mAnimator.phaseY

        val buffer = mBarBuffers[index]
        buffer.setPhases(phaseX, phaseY)
        buffer.setDataSet(index)
        buffer.setInverted(mChart.isInverted(dataSet.axisDependency))
        buffer.setBarWidth(mChart.barData.barWidth)
        buffer.feed(dataSet)
        trans.pointValuesToPixel(buffer.buffer)

        if (dataSet.gradientColor != null) {
            val gradientColor = dataSet.gradientColor
            val gradient = LinearGradient(0f, 0f, c.width.toFloat(), 0f,
                gradientColor.startColor, gradientColor.endColor,
                Shader.TileMode.MIRROR)
            mRenderPaint.shader = gradient
        } else {
            mRenderPaint.shader = null
            mRenderPaint.color = dataSet.color
        }

        var j = 0
        while (j < buffer.size()) {
            if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                j += 4
                continue
            }
            if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j])) {
                break
            }

            val barRect = RectF(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2], buffer.buffer[j + 3])
            c.drawRoundRect(barRect, barRadius, barRadius, mRenderPaint)
            if (drawBorder) {
                c.drawRoundRect(barRect, barRadius, barRadius, mBarBorderPaint)
            }

            val entryIndex = j / 4
            if (entryIndex < labels.size) {
                val label = labels[entryIndex]
                val textX = buffer.buffer[j] + Utils.convertDpToPixel(12f)
                val textY = (buffer.buffer[j + 1] + buffer.buffer[j + 3]) / 2f + Utils.calcTextHeight(textPaint, label) / 2f
                c.drawText(label, textX, textY, textPaint)
            }

            j += 4
        }
    }
}