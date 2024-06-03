package com.sofascore.scoreandroidacademy.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.sofascore.scoreandroidacademy.R

class CircleIconView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var circleColor: Int = ContextCompat.getColor(context, R.color.color_secondary_default)
    private val textPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.on_surface_on_surface_lv_1)

        textAlign = Paint.Align.CENTER
    }

    private var number: String = ""

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = (width / 2f)

        val paint = Paint().apply {
            this.color = circleColor
            style = Paint.Style.FILL
        }
        canvas.drawCircle(centerX, centerY, radius, paint)

        canvas.drawText(
            number,
            centerX,
            centerY - (textPaint.descent() + textPaint.ascent()) / 2,
            textPaint
        )
    }
}