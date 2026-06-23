package com.skywarriors.game.managers

import android.graphics.Canvas
import com.skywarriors.game.objects.Explosion

class ParticleManager {
    private val explosions = mutableListOf<Explosion>()

    fun addExplosion(x: Float, y: Float, size: Float = 1f) {
        explosions.add(Explosion(x, y, size))
    }

    fun update(deltaTime: Long) {
        val iter = explosions.iterator()
        while (iter.hasNext()) {
            val exp = iter.next()
            exp.update(deltaTime)
            if (!exp.isActive) iter.remove()
        }
    }

    fun render(canvas: Canvas) {
        for (exp in explosions) {
            exp.render(canvas)
        }
    }

    fun clear() {
        explosions.clear()
    }
}
