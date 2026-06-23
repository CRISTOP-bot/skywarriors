package com.skywarriors.game.managers

import androidx.compose.ui.graphics.drawscope.DrawScope
import com.skywarriors.game.objects.Bullet
import com.skywarriors.util.Constants

class BulletManager {
    private val playerBullets = mutableListOf<Bullet>()
    private val enemyBullets = mutableListOf<Bullet>()

    fun firePlayerBullet(x: Float, y: Float, spreadShot: Boolean) {
        val bullet = Bullet()
        bullet.init(x, y, playerBullet = true)
        playerBullets.add(bullet)

        if (spreadShot) {
            val spreadBullet1 = Bullet()
            spreadBullet1.init(x, y, -100f, -600f, playerBullet = true)
            playerBullets.add(spreadBullet1)

            val spreadBullet2 = Bullet()
            spreadBullet2.init(x, y, 100f, -600f, playerBullet = true)
            playerBullets.add(spreadBullet2)
        }
    }

    fun fireEnemyBullet(x: Float, y: Float, velX: Float, velY: Float) {
        val bullet = Bullet()
        bullet.init(x, y, velX, velY, playerBullet = false)
        enemyBullets.add(bullet)
    }

    fun fireEnemyBullet(x: Float, y: Float) {
        fireEnemyBullet(x, y, 0f, Constants.ENEMY_BULLET_SPEED.toFloat())
    }

    fun update(dt: Float, screenHeight: Float) {
        playerBullets.removeAll { !it.isActive }
        enemyBullets.removeAll { !it.isActive }

        for (b in playerBullets) {
            b.update(dt)
            if (b.y + b.height < 0 || kotlin.math.abs(b.x) > 2000f) {
                b.isActive = false
            }
        }

        for (b in enemyBullets) {
            b.update(dt)
            if (b.y > screenHeight + b.height || kotlin.math.abs(b.x) > 2000f) {
                b.isActive = false
            }
        }
    }

    fun DrawScope.renderPlayerBullets() {
        for (b in playerBullets) {
            b.render()
        }
    }

    fun DrawScope.renderEnemyBullets() {
        for (b in enemyBullets) {
            b.render()
        }
    }

    fun getPlayerBullets(): List<Bullet> = playerBullets
    fun getEnemyBullets(): List<Bullet> = enemyBullets

    fun reset() {
        playerBullets.clear()
        enemyBullets.clear()
    }
}
