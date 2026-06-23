package com.skywarriors.game.managers

import androidx.compose.ui.graphics.drawscope.DrawScope
import com.skywarriors.game.objects.PowerUp
import com.skywarriors.util.Constants
import kotlin.random.Random

class PowerUpManager {
    val powerUps = mutableListOf<PowerUp>()

    fun tryDropPowerUp(x: Float, y: Float) {
        if (Random.nextFloat() < Constants.POWERUP_CHANCE) {
            val type = PowerUp.PowerUpType.values()[Random.nextInt(PowerUp.PowerUpType.values().size)]
            val powerUp = PowerUp()
            powerUp.init(x, y, type)
            powerUps.add(powerUp)
        }
    }

    fun update(dt: Float, screenHeight: Float) {
        powerUps.removeAll { !it.isActive }

        for (p in powerUps) {
            p.update(dt)
            if (p.y > screenHeight + p.height) {
                p.isActive = false
            }
        }
    }

    fun DrawScope.render() {
        for (p in powerUps) {
            p.render()
        }
    }

    fun reset() {
        powerUps.clear()
    }
}
