/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jera.vision.presentation.view.vision.camera

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.google.mlkit.vision.text.Text
import kotlin.math.max
import kotlin.math.min

/**
 * Graphic instance for rendering TextBlock position, size, and ID within an associated graphic
 * overlay view.
 */
class TextGraphic(overlay: GraphicOverlay?, private val text: Text.Element) :
  GraphicOverlay.Graphic(overlay) {

  private val rectPaint: Paint = Paint()
  private val textPaint: Paint
  private val labelPaint: Paint

  init {
    rectPaint.color = MARKER_COLOR
    rectPaint.style = Paint.Style.STROKE
    rectPaint.strokeWidth = STROKE_WIDTH
    textPaint = Paint()
    textPaint.color = TEXT_COLOR
    textPaint.textSize = TEXT_SIZE
    labelPaint = Paint()
    labelPaint.color = MARKER_COLOR
    labelPaint.style = Paint.Style.FILL
    postInvalidate()
  }

  /** Draws the text block annotations for position, size, and raw value on the supplied canvas.  */
  override fun draw(canvas: Canvas) {
        // Draws the bounding box around the TextBlock.
        val rect = RectF(text.boundingBox)
        // If the image is flipped, the left will be translated to right, and the right to left.
        val x0 = translateX(rect.left)
        val x1 = translateX(rect.right)
        rect.left = min(x0, x1)
        rect.right = max(x0, x1)
        rect.top = translateY(rect.top)
        rect.bottom = translateY(rect.bottom)
        canvas.drawRect(rect, rectPaint)
        val lineHeight = TEXT_SIZE + 2 * STROKE_WIDTH
        val textWidth = textPaint.measureText(text.text)
        canvas.drawRect(
          rect.left - STROKE_WIDTH,
          rect.top - lineHeight,
          rect.left + textWidth + 2 * STROKE_WIDTH,
          rect.top,
          labelPaint
        )
        // Renders the text at the bottom of the box.
        canvas.drawText(
          text.text,
          rect.left,
          rect.top - STROKE_WIDTH,
          textPaint
        )
    }

  companion object {
    private const val TEXT_COLOR = Color.BLACK
    private const val MARKER_COLOR = Color.WHITE
    private const val TEXT_SIZE = 54.0f
    private const val STROKE_WIDTH = 4.0f
  }
}
