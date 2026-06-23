package com.skywarriors.game.managers

import com.skywarriors.game.objects.Enemy
import com.skywarriors.game.objects.PowerUp
import com.skywarriors.game.objects.PowerUpType
import com.skywarriors.util.Constants
import kotlin.random.Random

class PowerUpManager {
    private val powerUps = mutableListOf<PowerUp>()

    fun tryDropPowerUp(enemy: Enemy) {
        if (Random.nextFloat() > Constants.POWERUP_DROP_CHANCE) return

        val type = when (Random.nextInt(4)) {
            0 -> PowerUpType.SHIELD
            1 -> PowerUpType.RAPID_FIRE
            2 -> PowerUpType.SPREAD_SHOT
            3 -> PowerUpType.EXTRA_LIFE
            else -> PowerUpType.SHIELD
        }

        val powerUp = PowerUp(type)
        powerUp.x = enemy.x
        powerUp.y = enemy.y
        powerUps.add(powerUp)
    }

    fun update(deltaTime: Float, screenHeight: Float) {
        val iter = powerUps.iterator()
        while (iter.hasNext()) {
            val p = iter.next()
            p.update(deltaTime)
            if (p.y > screenHeight + p.height) iter.remove()
        }
    }

    fun getPowerUps(): List<PowerUp> = powerUps

    fun removePowerUp(powerUp: PowerUp) {
        powerUps.remove(powerUp)
    }

    fun clear() {
        powerUps.clear()
    }
}
