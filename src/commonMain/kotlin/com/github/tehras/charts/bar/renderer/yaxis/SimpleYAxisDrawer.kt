package com.github.tehras.charts.bar.renderer.yaxis

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import jatx.expense.manager.res.barChartLabelTextSize

typealias LabelFormatter = (value: Float) -> String

const val maxYCoeff = 1.25f

expect class SimpleYAxisDrawer(
  labelTextSize: TextUnit = barChartLabelTextSize,
  labelTextColor: Color = Color.Black,
  labelRatio: Int = 3,
  labelValueFormatter: LabelFormatter = { value -> "%.1f".format(value) },
  axisLineThickness: Dp = 1.dp,
  axisLineColor: Color = Color.Black
) : YAxisDrawer {
  internal val axisLinePaint: Paint

  override fun drawAxisLine(
    drawScope: DrawScope,
    canvas: Canvas,
    drawableArea: Rect
  )

  override fun drawAxisLabels(
    drawScope: DrawScope,
    canvas: Canvas,
    drawableArea: Rect,
    minValue: Float,
    maxValue: Float
  )
}