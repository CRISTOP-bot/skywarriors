package com.skywarriors.game.engine

import android.graphics.Canvas
import android.view.MotionEvent
import com.skywarriors.audio.SoundManager
import com.skywarriors.data.GameDatabase
import com.skywarriors.data.HighScore
import com.skywarriors.game.managers.BulletManager
import com.skywarriors.game.managers.EnemyManager
import com.skywarriors.game.managers.ParticleManager
import com.skywarriors.game.managers.PowerUpManager
import com.skywarriors.game.objects.Player
import com.skywarriors.game.objects.Star
import com.skywarriors.game.objects.Enemy
import com.skywarriors.game.ui.HUD
import com.skywarriors.game.ui.TouchHandler
import com.skywarriors.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.abs

class GameEngine(
    private val screenWidth: Float,
    private val screenHeight: Float,
    private val soundManager: SoundManager,
    private val database: GameDatabase
) {
    var state: GameState = GameState.MENU
        private set

    private val player = Player()
    private val hud = HUD()
    private val touchHandler = TouchHandler()
    private val bulletManager = BulletManager()
    private val enemyManager = EnemyManager()
    private val powerUpManager = PowerUpManager()
    private val particleManager = ParticleManager()

    private val stars = mutableListOf<Star>()
    private var lastShootTime: Long = 0
    private var highScore: Int = 0
    private var isNewHighScore: Boolean = false

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        Player.screenWidth = screenWidth
        Player.screenHeight = screenHeight
        resetGame()
        repeat(Constants.STAR_COUNT) {
            stars.add(Star(screenWidth, screenHeight))
        }
        loadHighScore()
    }

    private fun loadHighScore() {
        scope.launch {
            highScore = database.highScoreDao().getHighestScore()
        }
    }

    fun resetGame() {
        player.reset()
        player.x = screenWidth / 2
        player.y = screenHeight * 0.85f
        bulletManager.clear()
        enemyManager.clear()
        powerUpManager.clear()
        particleManager.clear()
        lastShootTime = 0
        isNewHighScore = false
    }

    fun startGame() {
        resetGame()
        state = GameState.PLAYING
    }

    fun handleTouch(event: MotionEvent) {
        val result = touchHandler.handleTouchEvent(event)

        when (state) {
            GameState.MENU -> {
                if (result.isDown) {
                    startGame()
                }
            }
            GameState.PLAYING -> {
                if (result.isDown) {
                    player.x = result.x
                    player.y = result.y
                }
                if (result.isDown) {
                    tryShoot()
                }
            }
            GameState.PAUSED -> {
                if (result.isDown) {
                    state = GameState.PLAYING
                }
            }
            GameState.GAME_OVER -> {
                if (result.isDown) {
                    state = GameState.MENU
                }
            }
        }
    }

    private fun tryShoot() {
        val now = System.currentTimeMillis()
        val cooldown = player.getShootCooldown()
        if (now - lastShootTime >= cooldown) {
            lastShootTime = now
            bulletManager.firePlayerBullet(player)
            soundManager.playShoot()
        }
    }

    fun update(deltaTime: Long) {
        when (state) {
            GameState.PLAYING -> updatePlaying(deltaTime)
            else -> {}
        }
    }

    private fun updatePlaying(deltaTime: Long) {
        val dt = deltaTime / 1000f

        player.velocityX = 0f
        player.velocityY = 0f
        val touchResult = touchHandler.lastResult
        if (touchResult != null && touchResult.isDown) {
            val dx = touchResult.x - player.x
            val dy = touchResult.y - player.y
            val dist = kotlin.math.sqrt(dx * dx + dy * dy)
            if (dist > 5f) {
                val speed = Constants.PLAYER_SPEED
                player.velocityX = (dx / dist) * speed
                player.velocityY = (dy / dist) * speed
            }
        }

        player.update(dt)
        player.updatePowerups(deltaTime)

        stars.forEach { it.update(dt) }

        val enemyBullets = enemyManager.update(deltaTime, screenHeight, player.score)
        enemyBullets.forEach { bulletManager.addEnemyBullet(it) }

        bulletManager.update(dt, screenHeight)

        powerUpManager.update(dt, screenHeight)

        particleManager.update(deltaTime)

        checkCollisions()

        if (player.lives <= 0) {
            state = GameState.GAME_OVER
            soundManager.playGameOver()
            saveScore()
        }
    }

    private fun checkCollisions() {
        val playerBounds = player.bounds

        val enemyBulletsIter = bulletManager.getEnemyBullets().iterator()
        while (enemyBulletsIter.hasNext()) {
            val bullet = enemyBulletsIter.next()
            if (!bullet.isActive) continue
            if (bullet.intersects(player)) {
                bullet.isActive = false
                enemyBulletsIter.remove()
                if (!player.hit()) {
                    soundManager.playHit()
                    particleManager.addExplosion(bullet.x, bullet.y, 0.5f)
                } else {
                    particleManager.addExplosion(player.x, player.y, 2f)
                }
            }
        }

        val enemies = enemyManager.getEnemies().toList()
        val enemiesToRemove = mutableListOf<Enemy>()

        for (enemy in enemies) {
            if (!enemy.isActive) continue

            if (enemy.intersects(player)) {
                if (!player.hit()) {
                    soundManager.playHit()
                }
                enemy.takeDamage(enemy.health)
                enemiesToRemove.add(enemy)
                particleManager.addExplosion(enemy.x, enemy.y, 1.5f)
                continue
            }

            val playerBullets = bulletManager.getPlayerBullets().toList()
            val bulletsToRemove = mutableListOf<com.skywarriors.game.objects.Bullet>()

            for (bullet in playerBullets) {
                if (!bullet.isActive) continue
                if (bullet.intersects(enemy)) {
                    bullet.isActive = false
                    bulletsToRemove.add(bullet)

                    if (enemy.takeDamage(1)) {
                        player.score += enemy.scoreValue
                        particleManager.addExplosion(enemy.x, enemy.y,
                            if (enemy.enemyType == com.skywarriors.game.objects.EnemyType.BOSS) 3f else 1f
                        )
                        soundManager.playExplosion()
                        powerUpManager.tryDropPowerUp(enemy)
                        enemiesToRemove.add(enemy)
                    }
                }
            }
            playerBullets.removeAll(bulletsToRemove)
        }

        enemiesToRemove.forEach { enemyManager.removeEnemy(it) }

        val powerUps = powerUpManager.getPowerUps()
        val powerUpsToRemove = mutableListOf<com.skywarriors.game.objects.PowerUp>()
        for (p in powerUps) {
            if (p.intersects(player)) {
                soundManager.playPowerUp()
                when (p.powerType) {
                    com.skywarriors.game.objects.PowerUpType.SHIELD -> player.activateShield()
                    com.skywarriors.game.objects.PowerUpType.RAPID_FIRE -> player.activateRapidFire()
                    com.skywarriors.game.objects.PowerUpType.SPREAD_SHOT -> player.activateSpreadShot()
                    com.skywarriors.game.objects.PowerUpType.EXTRA_LIFE -> {
                        if (player.lives < Constants.PLAYER_MAX_LIVES) player.lives++
                    }
                }
                powerUpsToRemove.add(p)
            }
        }
        powerUpsToRemove.forEach { powerUpManager.removePowerUp(it) }
    }

    private fun saveScore() {
        val finalScore = player.score
        scope.launch {
            val currentHigh = database.highScoreDao().getHighestScore()
            if (finalScore > currentHigh) {
                isNewHighScore = true
                highScore = finalScore
            }
            database.highScoreDao().insert(
                HighScore(name = "Player", score = finalScore)
            )
        }
    }

    fun render(canvas: Canvas) {
        stars.forEach { it.render(canvas) }

        when (state) {
            GameState.MENU -> {
                hud.renderMenu(canvas)
            }
            GameState.PLAYING -> {
                powerUpManager.getPowerUps().forEach { it.render(canvas) }
                bulletManager.getPlayerBullets().forEach { it.render(canvas) }
                bulletManager.getEnemyBullets().forEach { it.render(canvas) }
                enemyManager.getEnemies().forEach { it.render(canvas) }
                player.render(canvas)
                particleManager.render(canvas)

                val boss = enemyManager.getEnemies().find { it.enemyType == com.skywarriors.game.objects.EnemyType.BOSS }
                hud.renderPlaying(canvas, player, boss)
            }
            GameState.PAUSED -> {
                powerUpManager.getPowerUps().forEach { it.render(canvas) }
                bulletManager.getPlayerBullets().forEach { it.render(canvas) }
                bulletManager.getEnemyBullets().forEach { it.render(canvas) }
                enemyManager.getEnemies().forEach { it.render(canvas) }
                player.render(canvas)
                particleManager.render(canvas)
                hud.renderPaused(canvas)
            }
            GameState.GAME_OVER -> {
                hud.renderGameOver(canvas, player.score, highScore, isNewHighScore)
            }
        }
    }

    fun togglePause() {
        if (state == GameState.PLAYING) state = GameState.PAUSED
        else if (state == GameState.PAUSED) state = GameState.PLAYING
    }

    fun resize(width: Float, height: Float) {
        stars.forEach { it.resize(width, height) }
    }
}
