package com.skywarriors.game.managers

import androidx.compose.ui.graphics.drawscope.DrawScope
import com.skywarriors.game.objects.Explosion

class ParticleManager {
    private val explosions = mutableListOf<Explosion>()

    fun addExplosion(x: Float, y: Float, sizeMultiplier: Float = 1f) {
        val explosion = Explosion()
        explosion.init(x, y, sizeMultiplier)
        explosions.add(explosion)
    }

    fun update(dt: Float) {
        explosions.removeAll { !it.isActive }
        for (e in explosions) {
            e.update(dt)
        }
    }

    fun DrawScope.render() {
        for (e in explosions) {
            e.render()
        }
    }

    fun reset() {
        explosions.clear()
    }
}
