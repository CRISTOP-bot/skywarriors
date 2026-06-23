package com.skywarriors.game.engine

import com.skywarriors.audio.SoundManager
import com.skywarriors.data.HighScoreManager
import com.skywarriors.game.GameState
import com.skywarriors.game.managers.BulletManager
import com.skywarriors.game.managers.EnemyManager
import com.skywarriors.game.managers.ParticleManager
import com.skywarriors.game.managers.PowerUpManager
import com.skywarriors.game.objects.Player
import com.skywarriors.game.objects.Star
import com.skywarriors.game.ui.InputHandler
import com.skywarriors.util.Constants

class GameEngine {
    var gameState: GameState = GameState.MENU
    var screenWidth: Float = 1080f
    var screenHeight: Float = 1920f

    val player = Player()
    val bulletManager = BulletManager()
    val enemyManager = EnemyManager()
    val powerUpManager = PowerUpManager()
    val particleManager = ParticleManager()
    val highScoreManager = HighScoreManager()
    val inputHandler = InputHandler()
    val soundManager = SoundManager()

    val stars = mutableListOf<Star>()
    private var isPausedFromSystem = false

    fun init(width: Float, height: Float) {
        screenWidth = width
        screenHeight = height
        player.init(width, height)
        enemyManager.init(width)

        stars.clear()
        for (i in 0 until Constants.STAR_COUNT) {
            val star = Star()
            star.init(width, height)
            stars.add(star)
        }

        soundManager.init()
        gameState = GameState.MENU
    }

    fun handleMenuTap() {
        if (gameState == GameState.MENU) {
            startGame()
        }
    }

    fun handleGameOverTap() {
        if (gameState == GameState.GAME_OVER) {
            resetGame()
        }
    }

    fun handlePauseToggle() {
        when (gameState) {
            GameState.PLAYING -> {
                gameState = GameState.PAUSED
                isPausedFromSystem = true
            }
            GameState.PAUSED -> {
                gameState = GameState.PLAYING
                isPausedFromSystem = false
            }
            else -> {}
        }
    }

    fun startGame() {
        player.init(screenWidth, screenHeight)
        bulletManager.reset()
        enemyManager.init(screenWidth)
        powerUpManager.reset()
        particleManager.reset()
        gameState = GameState.PLAYING
    }

    fun resetGame() {
        gameState = GameState.MENU
    }

    fun update(dt: Float) {
        val dtMillis = (dt * 1000).toLong()

        when (gameState) {
            GameState.MENU -> {}
            GameState.PLAYING -> updatePlaying(dt, dtMillis)
            GameState.PAUSED -> {}
            GameState.GAME_OVER -> {}
        }
    }

    private fun updatePlaying(dt: Float, dtMillis: Long) {
        val input = inputHandler.getInput()

        if (input.isDown) {
            player.moveTo(input.x, input.y, dt)
            if (player.canShoot()) {
                bulletManager.firePlayerBullet(player.x, player.y, player.spreadShotActive)
                player.resetShootCooldown()
            }
        } else {
            player.velocityX = 0f
            player.velocityY = 0f
        }

        player.update(dt, System.currentTimeMillis())

        for (star in stars) {
            star.update(dt)
        }

        enemyManager.update(dt, dtMillis).forEach { bulletData ->
            bulletManager.fireEnemyBullet(bulletData.x, bulletData.y, bulletData.velX, bulletData.velY)
        }

        bulletManager.update(dt, screenHeight)
        powerUpManager.update(dt, screenHeight)
        particleManager.update(dt)

        enemyManager.trySpawnBoss(player.score)

        checkCollisions()
    }

    private fun checkCollisions() {
        val playerBounds = player.bounds

        for (bullet in bulletManager.getEnemyBullets()) {
            if (bullet.isActive && bullet.bounds.overlaps(playerBounds)) {
                bullet.isActive = false
                if (player.hit()) {
                    gameOver()
                    return
                }
                particleManager.addExplosion(bullet.x, bullet.y, 0.5f)
            }
        }

        for (enemy in enemyManager.enemies) {
            if (!enemy.isActive) continue
            if (enemy.bounds.overlaps(playerBounds)) {
                if (player.hit()) {
                    gameOver()
                    return
                }
                enemy.isActive = false
                particleManager.addExplosion(enemy.x, enemy.y, if (enemy.type == com.skywarriors.game.objects.Enemy.EnemyType.BOSS) 3f else 1f)
            }
        }

        for (bullet in bulletManager.getPlayerBullets()) {
            if (!bullet.isActive) continue
            for (enemy in enemyManager.enemies) {
                if (!enemy.isActive) continue
                if (bullet.bounds.overlaps(enemy.bounds)) {
                    bullet.isActive = false
                    if (enemy.takeDamage(1)) {
                        val scoreValue = when (enemy.type) {
                            com.skywarriors.game.objects.Enemy.EnemyType.BASIC -> 100
                            com.skywarriors.game.objects.Enemy.EnemyType.FAST -> 150
                            com.skywarriors.game.objects.Enemy.EnemyType.TANK -> 300
                            com.skywarriors.game.objects.Enemy.EnemyType.BOSS -> 1000
                        }
                        player.score += scoreValue

                        particleManager.addExplosion(
                            enemy.x, enemy.y,
                            if (enemy.type == com.skywarriors.game.objects.Enemy.EnemyType.BOSS) 3f else 1f
                        )
                        powerUpManager.tryDropPowerUp(enemy.x, enemy.y)
                    }
                    break
                }
            }
        }

        for (powerUp in powerUpManager.powerUps) {
            if (powerUp.isActive && powerUp.bounds.overlaps(playerBounds)) {
                powerUp.isActive = false
                player.activatePowerUp(powerUp.powerUpType)
            }
        }
    }

    private fun gameOver() {
        highScoreManager.addScore("Player", player.score)
        gameState = GameState.GAME_OVER
    }

    fun getBoss(): com.skywarriors.game.objects.Enemy? {
        return enemyManager.enemies.find { it.type == com.skywarriors.game.objects.Enemy.EnemyType.BOSS && it.isActive }
    }
}
