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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.tehras.charts.piechart.utils.toLegacyInt
import org.jetbrains.skia.Font
import org.jetbrains.skia.TextLine
import kotlin.math.max
import kotlin.math.roundToInt

typealias LabelFormatter = (value: Float) -> String

class SimpleYAxisDrawer(
  private val labelTextSize: TextUnit = 12.sp,
  private val labelTextColor: Color = Color.Black,
  private val labelRatio: Int = 3,
  private val labelValueFormatter: LabelFormatter = { value -> "%.1f".format(value) },
  private val axisLineThickness: Dp = 1.dp,
  private val axisLineColor: Color = Color.Black
) : YAxisDrawer {
  private val axisLinePaint = Paint().apply {
    isAntiAlias = true
    color = axisLineColor
    style = PaintingStyle.Stroke
  }
//  private val textPaint = android.graphics.Paint().apply {
//    isAntiAlias = true
//    color = labelTextColor.toLegacyInt()
//  }
//  private val textBounds = android.graphics.Rect()

  override fun drawAxisLine(
    drawScope: DrawScope,
    canvas: Canvas,
    drawableArea: Rect
  ) = with(drawScope) {
    val lineThickness = axisLineThickness.toPx()
    val x = drawableArea.right - (lineThickness / 2f)

    canvas.drawLine(
      p1 = Offset(
        x = x,
        y = drawableArea.top
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

  override fun drawAxisLabels(
    drawScope: DrawScope,
    canvas: Canvas,
    drawableArea: Rect,
    minValue: Float,
    maxValue: Float
  ) = with(drawScope) {
//    val labelPaint = textPaint.apply {
//      textSize = labelTextSize.toPx()
//      textAlign = android.graphics.Paint.Align.RIGHT
//    }
    val minLabelHeight = 100f
    val totalHeight = drawableArea.height

    val step = 10000f
    val labelCount = ((maxValue - minValue) / step).toInt() + 1

    val actualMax = (labelCount - 1) * step + minValue
    val actualTotalHeight = (labelCount - 1) * step / (maxValue - minValue) * totalHeight

    println(minValue)
    println(maxValue)
    println(actualMax)

    for (i in 0..labelCount) {
      val value = minValue + (i * step)

      val label = labelValueFormatter(value)
      val x = drawableArea.right - axisLineThickness.toPx() - 50f

//      labelPaint.getTextBounds(label, 0, label.length, textBounds)

      val y =
        drawableArea.bottom - (i * (actualTotalHeight / labelCount))// + (textBounds.height() / 2f)

      canvas.nativeCanvas.drawTextLine(
        TextLine.Companion.make(label, Font()),
        x,
        y,
        org.jetbrains.skia.Paint()
      )
    }
  }
}