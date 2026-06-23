package com.skywarriors.game.objects

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope

abstract class GameObject {
    var x: Float = 0f
    var y: Float = 0f
    var width: Float = 0f
    var height: Float = 0f
    var velocityX: Float = 0f
    var velocityY: Float = 0f
    var isActive: Boolean = true

    val bounds: Rect
        get() = Rect(
            offset = Offset(x - width / 2, y - height / 2),
            size = Size(width, height)
        )

    val centerX: Float get() = x
    val centerY: Float get() = y

    open fun update(deltaTime: Float) {
        x += velocityX * deltaTime
        y += velocityY * deltaTime
    }

    fun intersects(other: GameObject): Boolean {
        return bounds.overlaps(other.bounds)
    }

    abstract fun DrawScope.render()
}
