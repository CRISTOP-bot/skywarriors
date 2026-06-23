package com.skywarriors.game.objects

import android.graphics.Canvas
import android.graphics.RectF

abstract class GameObject {
    var x: Float = 0f
    var y: Float = 0f
    var width: Float = 0f
    var height: Float = 0f
    var velocityX: Float = 0f
    var velocityY: Float = 0f
    var isActive: Boolean = true

    val bounds: RectF
        get() = RectF(x - width / 2, y - height / 2, x + width / 2, y + height / 2)

    open fun update(deltaTime: Float) {
        x += velocityX * deltaTime
        y += velocityY * deltaTime
    }

    abstract fun render(canvas: Canvas)

    fun intersects(other: GameObject): Boolean {
        return RectF.intersects(bounds, other.bounds)
    }

    fun centerX(): Float = x
    fun centerY(): Float = y
}
