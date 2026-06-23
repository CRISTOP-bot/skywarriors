package com.skywarriors.game.objects

import android.graphics.Canvas
import android.graphics.Paint
import com.skywarriors.util.Constants
import kotlin.random.Random

class Star(screenWidth: Float, screenHeight: Float) {
    var x: Float = Random.nextFloat() * screenWidth
    var y: Float = Random.nextFloat() * screenHeight
    val speed: Float = Constants.STAR_BASE_SPEED * (0.3f + Random.nextFloat() * 0.7f)
    val size: Float = 1f + Random.nextFloat() * 2.5f
    val alpha: Int = (100 + Random.nextInt(156)).coerceIn(0, 255)
    private var screenW: Float = screenWidth
    private var screenH: Float = screenHeight

    private val paint = Paint().apply {
        color = android.graphics.Color.argb(alpha, 255, 255, 255)
    }

    fun update(deltaTime: Float) {
        y += speed * deltaTime
        if (y > screenH) {
            y = 0f
            x = Random.nextFloat() * screenW
        }
    }

    fun render(canvas: Canvas) {
        paint.alpha = alpha
        canvas.drawCircle(x, y, size, paint)
    }

    fun resize(width: Float, height: Float) {
        screenW = width
        screenH = height
    }
}
