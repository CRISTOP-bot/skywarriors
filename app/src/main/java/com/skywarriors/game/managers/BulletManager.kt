package com.skywarriors.game.managers

import com.skywarriors.game.objects.Bullet
import com.skywarriors.game.objects.Player
import com.skywarriors.util.Constants

class BulletManager {
    private val playerBullets = mutableListOf<Bullet>()
    private val enemyBullets = mutableListOf<Bullet>()

    fun firePlayerBullet(player: Player): List<Bullet> {
        val bullets = mutableListOf<Bullet>()
        val startY = player.y - player.height / 2

        when {
            player.spreadShotActive -> {
                for (i in -2..2) {
                    Bullet(player.x + i * 12f, startY, true).apply {
                        velocityX = i * 60f
                        bullets.add(this)
                    }
                }
            }
            else -> {
                bullets.add(Bullet(player.x, startY, true))
            }
        }

        playerBullets.addAll(bullets)
        return bullets
    }

    fun addEnemyBullet(bullet: Bullet) {
        enemyBullets.add(bullet)
    }

    fun update(deltaTime: Float, screenHeight: Float) {
        val iter1 = playerBullets.iterator()
        while (iter1.hasNext()) {
            val b = iter1.next()
            b.update(deltaTime)
            if (b.y < -b.height || b.y > screenHeight + b.height) iter1.remove()
            else if (kotlin.math.abs(b.x) > 2000f) iter1.remove()
        }

        val iter2 = enemyBullets.iterator()
        while (iter2.hasNext()) {
            val b = iter2.next()
            b.update(deltaTime)
            if (b.y < -b.height || b.y > screenHeight + b.height) iter2.remove()
        }
    }

    fun getPlayerBullets(): List<Bullet> = playerBullets
    fun getEnemyBullets(): List<Bullet> = enemyBullets

    fun clear() {
        playerBullets.clear()
        enemyBullets.clear()
    }
}
