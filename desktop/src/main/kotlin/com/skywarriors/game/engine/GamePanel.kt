package com.skywarriors.game.engine

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.rememberTextMeasurer
import com.skywarriors.game.GameState
import com.skywarriors.game.objects.Enemy
import com.skywarriors.game.ui.drawBossHealthBar
import com.skywarriors.game.ui.drawHUD

@Composable
fun GamePanel(modifier: Modifier = Modifier) {
    val engine = remember { GameEngine() }
    val textMeasurer = rememberTextMeasurer()
    var gameState by remember { mutableStateOf(GameState.MENU) }
    var currentTime by remember { mutableLongStateOf(0L) }

    LaunchedEffect(Unit) {
        engine.init(1080f, 1920f)
        gameState = engine.gameState

        var lastFrameTime = withFrameMillis { it }
        while (true) {
            val frameTime = withFrameMillis { it }
            val dt = ((frameTime - lastFrameTime) / 1000f).coerceAtMost(0.05f)
            lastFrameTime = frameTime
            currentTime = frameTime

            engine.update(dt)
            gameState = engine.gameState
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D2B))
            .onPointerEvent(PointerEventType.Press) { event ->
                val pos = event.changes.firstOrNull() ?: return@onPointerEvent
                val mx = pos.position.x
                val my = pos.position.y

                when (gameState) {
                    GameState.MENU -> engine.handleMenuTap()
                    GameState.PLAYING -> engine.inputHandler.onMouseDown(mx, my)
                    GameState.PAUSED -> {}
                    GameState.GAME_OVER -> engine.handleGameOverTap()
                }
            }
            .onPointerEvent(PointerEventType.Release) {
                engine.inputHandler.onMouseUp()
            }
            .onPointerEvent(PointerEventType.Move) { event ->
                val pos = event.changes.firstOrNull() ?: return@onPointerEvent
                engine.inputHandler.onMouseMove(pos.position.x, pos.position.y)
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(Color(0xFF0D0D2B), size = size)

            when (gameState) {
                GameState.MENU, GameState.GAME_OVER -> {
                    drawHUD(textMeasurer, engine.player, gameState, engine.highScoreManager, currentTime, false)
                }
                GameState.PLAYING, GameState.PAUSED -> {
                    engine.stars.forEach { it.render() }
                    engine.powerUpManager.render()
                    engine.enemyManager.render()
                    engine.bulletManager.renderPlayerBullets()
                    engine.bulletManager.renderEnemyBullets()
                    engine.player.render()
                    engine.particleManager.render()
                    drawHUD(textMeasurer, engine.player, gameState, engine.highScoreManager, currentTime, false)

                    val boss = engine.getBoss()
                    if (boss != null && boss.type == Enemy.EnemyType.BOSS) {
                        drawBossHealthBar(textMeasurer, boss)
                    }
                }
            }
        }
    }
}
