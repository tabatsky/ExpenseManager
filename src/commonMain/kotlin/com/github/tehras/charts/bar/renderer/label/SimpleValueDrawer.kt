package com.github.tehras.charts.bar.renderer.label

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.TextUnit
import jatx.expense.manager.res.barChartLabelTextSize

expect class SimpleValueDrawer(
  drawLocation: DrawLocation = DrawLocation.XAxis,
  labelTextSize: TextUnit = barChartLabelTextSize,
  labelTextColor: Color = Color.Black
) : LabelDrawer {
  internal val _labelTextArea: Float?
//  private val paint = android.graphics.Paint().apply {
//    this.textAlign = android.graphics.Paint.Align.CENTER
//    this.color = labelTextColor.toLegacyInt()
//  }

  override fun requiredAboveBarHeight(drawScope: DrawScope): Float

  override fun drawLabel(
    drawScope: DrawScope,
    canvas: Canvas,
    label: String,
    barArea: Rect,
    xAxisArea: Rect
  )

  override fun requiredXAxisHeight(drawScope: DrawScope): Float

  internal fun labelTextHeight(drawScope: DrawScope): Float
}

enum class DrawLocation { Inside, Outside, XAxis }