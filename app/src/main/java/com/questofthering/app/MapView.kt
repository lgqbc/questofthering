package com.questofthering.app

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.caverock.androidsvg.SVG
import java.io.File

class MapView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var svg: SVG? = null
    private var currentPosition: Float = 0f
    private val markerPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    init {
        loadMap()
    }

    private fun loadMap() {
        try {
            // Load from assets
            val inputStream = context.assets.open("map.svg")
            svg = SVG.getFromInputStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updatePosition(progress: Float) {
        currentPosition = progress.coerceIn(0f, 1f)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        svg?.let {
            // Set the view box to match our view size
            it.documentWidth = width.toFloat()
            it.documentHeight = height.toFloat()

            // Render the SVG
            it.renderToCanvas(canvas)

            // Draw position marker
            // For now, draw a simple marker - we'll enhance this with actual route data
            val markerX = width * 0.3f  // Shire is roughly on the left side
            val markerY = height * 0.5f

            canvas.drawCircle(markerX, markerY, 15f, markerPaint)
        }

        // If SVG didn't load, show a placeholder
        if (svg == null) {
            val paint = Paint().apply {
                color = Color.GRAY
                textSize = 40f
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText(
                "Map Loading...",
                width / 2f,
                height / 2f,
                paint
            )
        }
    }
}
