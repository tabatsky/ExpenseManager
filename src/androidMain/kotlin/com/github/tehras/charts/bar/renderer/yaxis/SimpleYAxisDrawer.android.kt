package com.github.tehras.charts.bar.renderer.yaxis

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import com.github.tehras.charts.piechart.utils.toLegacyInt

actual class SimpleYAxisDrawer actual constructor(
    private val labelTextSize: TextUnit,
    private val labelTextColor: Color,
    private val labelRatio: Int,
    private val labelValueFormatter: LabelFormatter,
    private val axisLineThickness: Dp,
    private val axisLineColor: Color
) : YAxisDrawer {
    private val textPaint = android.graphics.Paint().apply {
        isAntiAlias = true
        color = labelTextColor.toLegacyInt()
    }
    private val textBounds = android.graphics.Rect()

    internal actual val axisLinePaint: Paint = Paint().apply {
        isAntiAlias = true
        color = axisLineColor
        style = PaintingStyle.Stroke
    }

    actual override fun drawAxisLine(
        drawScope: DrawScope,
        canvas: Canvas,
        drawableArea: Rect
    )  = with(drawScope) {
        val lineThickness = axisLineThickness.toPx()
        val x = drawableArea.right - (lineThickness / 2f)

        val height = drawableArea.bottom - drawableArea.top

        canvas.drawLine(
            p1 = Offset(
                x = x,
                y = drawableArea.bottom - height * maxYCoeff
            ),
            p2 = Offset(
                x = x,
                y = drawableArea.bottom
            ),
            paint = axisLinePaint.apply {
                strokeWidth = lineThickness
            }
        )
    }

    actual override fun drawAxisLabels(
        drawScope: DrawScope,
        canvas: Canvas,
        drawableArea: Rect,
        minValue: Float,
        maxValue: Float
    )  = with(drawScope) {
        val labelPaint = textPaint.apply {
            textSize = labelTextSize.toPx()
            textAlign = android.graphics.Paint.Align.RIGHT
        }
        val minLabelHeight = 100f
        val totalHeight = drawableArea.height

        if ((maxValue - minValue).toInt() == 0) return@with

        val step = 10000 * ((((maxValue - minValue).toInt() - 1) / 100000) + 1)
        val shift = if ((maxValue - minValue).toInt() % step == 0) 1 else 2
        val baseLabelCount = ((maxValue - minValue).toInt() / step).plus(shift)
        val labelCount = baseLabelCount.minus(shift).times(maxYCoeff).toInt().plus(shift)

        val actualMax = (labelCount - 1) * step + minValue
        val hCoeff =  (maxValue - minValue) / (actualMax - minValue)
        val actualTotalHeight = totalHeight / hCoeff

        for (i in 0 until  labelCount) {
            val value = minValue + (i * step)

            val label = labelValueFormatter(value)
            val x = drawableArea.right - axisLineThickness.toPx() - 50f

            labelPaint.getTextBounds(label, 0, label.length, textBounds)

            val y =
                drawableArea.bottom - (i * (actualTotalHeight / (labelCount - 1))) // + font.size / 2 // + (textBounds.height() / 2f)

            canvas.nativeCanvas.drawText(label, x, y, labelPaint)
        }
    }
}