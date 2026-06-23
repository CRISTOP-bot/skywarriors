package com.skywarriors.game.objects

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import com.skywarriors.util.Constants
import kotlin.random.Random

class Star {
    var x: Float = 0f
    var y: Float = 0f
    var speed: Float = 0f
    var size: Float = 0f
    var alpha: Float = 0f
    var screenWidth: Float = 1080f
    var screenHeight: Float = 1920f

    fun init(screenW: Float, screenH: Float) {
        screenWidth = screenW
        screenHeight = screenH
        x = Random.nextFloat() * screenW
        y = Random.nextFloat() * screenH
        speed = Constants.STAR_BASE_SPEED.toFloat() * (0.5f + Random.nextFloat())
        size = 1f + Random.nextFloat() * 2f
        alpha = 0.3f + Random.nextFloat() * 0.7f
    }

    fun update(dt: Float) {
        y += speed * dt
        if (y > screenHeight) {
            y = -size
            x = Random.nextFloat() * screenWidth
        }
    }

    fun DrawScope.render() {
        drawCircle(
            color = Color.White,
            radius = size,
            center = Offset(x, y),
            alpha = alpha,
            style = Fill
        )
    }
}
