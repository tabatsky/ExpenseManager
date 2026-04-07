package com.github.tehras.charts.bar.renderer.label

import android.graphics.Paint
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.TextUnit
import com.github.tehras.charts.piechart.utils.toLegacyInt

actual class SimpleValueDrawer actual constructor(
    private val drawLocation: DrawLocation,
    private val labelTextSize: TextUnit,
    private val labelTextColor: Color
) : LabelDrawer {
    internal actual val _labelTextArea: Float? = null

    private val paint: Paint
        get() = Paint().apply {
            this.textAlign = Paint.Align.CENTER
            this.color = labelTextColor.toLegacyInt()
        }

    actual override fun requiredAboveBarHeight(drawScope: DrawScope): Float = when (drawLocation) {
        DrawLocation.Outside -> (3f / 2f) * labelTextHeight(drawScope)
        DrawLocation.Inside,
        DrawLocation.XAxis -> 0f
    }

    actual override fun drawLabel(
        drawScope: DrawScope,
        canvas: Canvas,
        label: String,
        barArea: Rect,
        xAxisArea: Rect
    ) = with(drawScope) {
        // TODO: исправить

        val xCenter = barArea.left + 60f

//        val yCenter = when (drawLocation) {
//            DrawLocation.Inside -> (barArea.top + barArea.bottom) / 2
//            DrawLocation.Outside -> (barArea.top) - labelTextSize.toPx() / 2
//            DrawLocation.XAxis -> barArea.bottom + labelTextHeight(drawScope)
//        }

        val yCenter = barArea.top + (barArea.width / 2)

        rotate(-90f, barArea.topCenter) {
            canvas.nativeCanvas.drawText(label, xCenter, yCenter, paint(drawScope))
        }
    }

    actual override fun requiredXAxisHeight(drawScope: DrawScope): Float = when (drawLocation) {
        DrawLocation.XAxis -> labelTextHeight(drawScope)
        DrawLocation.Inside,
        DrawLocation.Outside -> 0f
    }

    internal actual fun labelTextHeight(drawScope: DrawScope): Float = with(drawScope) {
        _labelTextArea ?: ((3f / 2f) * labelTextSize.toPx())
    }

    private fun paint(drawScope: DrawScope) = with(drawScope) {
        paint.apply {
            this.textSize = labelTextSize.toPx()
        }
    }
}

