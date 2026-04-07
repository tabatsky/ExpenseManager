package com.github.tehras.charts.bar.renderer.label

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.TextUnit
import org.jetbrains.skia.Font
import org.jetbrains.skia.Paint
import org.jetbrains.skia.TextLine


actual class SimpleValueDrawer actual constructor(
    private val drawLocation: DrawLocation,
    private val labelTextSize: TextUnit,
    private val labelTextColor: Color
) : LabelDrawer {
    internal actual val _labelTextArea: Float? = null

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
    )  = with(drawScope) {
        val xCenter = barArea.left + 10f

//    val yCenter = when (drawLocation) {
//      Inside -> (barArea.top + barArea.bottom) / 2
//      Outside -> (barArea.top) - labelTextSize.toPx() / 2
//      XAxis -> barArea.bottom + labelTextHeight(drawScope)
//    }

        val yCenter = barArea.top + (barArea.width / 2)

        rotate(-90f, barArea.topCenter) {
            canvas.nativeCanvas.drawTextLine(
                TextLine.Companion.make(label, Font(null, 18f)),
                xCenter,
                yCenter,
                Paint()
            )
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
}
