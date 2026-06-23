package com.skywarriors.game.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.skywarriors.data.HighScoreManager
import com.skywarriors.game.GameState
import com.skywarriors.game.objects.Enemy
import com.skywarriors.game.objects.Player

fun DrawScope.drawHUD(
    textMeasurer: TextMeasurer,
    player: Player,
    gameState: GameState,
    highScoreManager: HighScoreManager,
    currentTime: Long,
    isPausedFromSystem: Boolean
) {
    when (gameState) {
        GameState.MENU -> drawMenuScreen(textMeasurer, currentTime)
        GameState.PLAYING -> drawPlayingHUD(textMeasurer, player, currentTime)
        GameState.PAUSED -> drawPausedScreen(textMeasurer, isPausedFromSystem)
        GameState.GAME_OVER -> drawGameOverScreen(textMeasurer, player, currentTime, highScoreManager)
    }
}

private fun DrawScope.drawMenuScreen(textMeasurer: TextMeasurer, currentTime: Long) {
    val titleText = "SKY WARRIORS"
    val titleStyle = TextStyle(
        color = Color(0xFF00E5FF),
        fontSize = 48.sp,
        fontWeight = FontWeight.Bold
    )
    val measured = textMeasurer.measure(titleText, titleStyle)
    drawText(
        textLayoutResult = measured,
        topLeft = Offset(size.width / 2 - measured.size.width / 2, size.height / 3 - measured.size.height / 2)
    )

    if ((currentTime / 500) % 2 == 0L) {
        val tapText = "TAP TO FLY"
        val tapStyle = TextStyle(color = Color.White, fontSize = 24.sp)
        val measuredTap = textMeasurer.measure(tapText, tapStyle)
        drawText(
            textLayoutResult = measuredTap,
            topLeft = Offset(size.width / 2 - measuredTap.size.width / 2, size.height / 2 - measuredTap.size.height / 2)
        )
    }

    val controlsText = "Mouse: Move & Shoot | ESC: Pause"
    val controlsStyle = TextStyle(color = Color(0xFF888888), fontSize = 14.sp)
    val measuredControls = textMeasurer.measure(controlsText, controlsStyle)
    drawText(
        textLayoutResult = measuredControls,
        topLeft = Offset(size.width / 2 - measuredControls.size.width / 2, size.height * 0.65f)
    )
}

private fun DrawScope.drawPlayingHUD(textMeasurer: TextMeasurer, player: Player, currentTime: Long) {
    val scoreStyle = TextStyle(color = Color.White, fontSize = 20.sp)
    val scoreText = "Score: ${player.score}"
    val measuredScore = textMeasurer.measure(scoreText, scoreStyle)
    drawText(
        textLayoutResult = measuredScore,
        topLeft = Offset(20f, 20f)
    )

    for (i in 0 until player.lives) {
        drawCircle(
            color = Color(0xFFFF1744),
            radius = 8f,
            center = Offset(size.width - 30f - i * 24f, 28f)
        )
    }

    var statusY = 50f
    if (player.shieldActive) {
        val shieldText = "SHIELD"
        val shieldStyle = TextStyle(color = Color(0xFF448AFF), fontSize = 14.sp)
        val measured = textMeasurer.measure(shieldText, shieldStyle)
        drawText(textLayoutResult = measured, topLeft = Offset(20f, statusY))
        statusY += 20f
    }
    if (player.rapidFireActive) {
        val rapidText = "RAPID FIRE"
        val rapidStyle = TextStyle(color = Color(0xFFFF6D00), fontSize = 14.sp)
        val measured = textMeasurer.measure(rapidText, rapidStyle)
        drawText(textLayoutResult = measured, topLeft = Offset(20f, statusY))
        statusY += 20f
    }
    if (player.spreadShotActive) {
        val spreadText = "SPREAD SHOT"
        val spreadStyle = TextStyle(color = Color(0xFFD500F9), fontSize = 14.sp)
        val measured = textMeasurer.measure(spreadText, spreadStyle)
        drawText(textLayoutResult = measured, topLeft = Offset(20f, statusY))
    }
}

fun DrawScope.drawBossHealthBar(textMeasurer: TextMeasurer, boss: Enemy) {
    val barWidth = 300f
    val barHeight = 16f
    val barX = size.width / 2 - barWidth / 2
    val barY = 60f

    val healthPercent = boss.getHealthPercent()

    drawRoundRect(
        color = Color(0xFF333333),
        topLeft = Offset(barX, barY),
        size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f)
    )
    drawRoundRect(
        color = Color(0xFFFF1744),
        topLeft = Offset(barX, barY),
        size = androidx.compose.ui.geometry.Size(barWidth * healthPercent, barHeight),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f)
    )

    val bossStyle = TextStyle(color = Color.White, fontSize = 12.sp)
    val measured = textMeasurer.measure("BOSS", bossStyle)
    drawText(
        textLayoutResult = measured,
        topLeft = Offset(size.width / 2 - measured.size.width / 2, barY - 18f)
    )
}

private fun DrawScope.drawPausedScreen(textMeasurer: TextMeasurer, isPausedFromSystem: Boolean) {
    val pausedText = "PAUSED"
    val style = TextStyle(color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Bold)
    val measured = textMeasurer.measure(pausedText, style)
    drawText(
        textLayoutResult = measured,
        topLeft = Offset(size.width / 2 - measured.size.width / 2, size.height / 2 - measured.size.height / 2)
    )

    val resumeText = if (isPausedFromSystem) "Press ESC to resume" else "Press ESC to resume"
    val resumeStyle = TextStyle(color = Color(0xFF888888), fontSize = 18.sp)
    val measuredResume = textMeasurer.measure(resumeText, resumeStyle)
    drawText(
        textLayoutResult = measuredResume,
        topLeft = Offset(size.width / 2 - measuredResume.size.width / 2, size.height / 2 + 40f)
    )
}

private fun DrawScope.drawGameOverScreen(
    textMeasurer: TextMeasurer,
    player: Player,
    currentTime: Long,
    highScoreManager: HighScoreManager
) {
    val gameOverText = "GAME OVER"
    val gameOverStyle = TextStyle(color = Color(0xFFFF1744), fontSize = 48.sp, fontWeight = FontWeight.Bold)
    val measuredGO = textMeasurer.measure(gameOverText, gameOverStyle)
    drawText(
        textLayoutResult = measuredGO,
        topLeft = Offset(size.width / 2 - measuredGO.size.width / 2, size.height / 3 - measuredGO.size.height / 2)
    )

    val scoreText = "Score: ${player.score}"
    val scoreStyle = TextStyle(color = Color.White, fontSize = 28.sp)
    val measuredScore = textMeasurer.measure(scoreText, scoreStyle)
    drawText(
        textLayoutResult = measuredScore,
        topLeft = Offset(size.width / 2 - measuredScore.size.width / 2, size.height / 2 - measuredScore.size.height / 2)
    )

    val isNewHigh = highScoreManager.isNewHighScore(player.score)
    if (isNewHigh && (currentTime / 500) % 2 == 0L) {
        val highText = "NEW HIGH SCORE!"
        val highStyle = TextStyle(color = Color(0xFFFFD600), fontSize = 24.sp, fontWeight = FontWeight.Bold)
        val measuredHigh = textMeasurer.measure(highText, highStyle)
        drawText(
            textLayoutResult = measuredHigh,
            topLeft = Offset(size.width / 2 - measuredHigh.size.width / 2, size.height / 2 + 50f)
        )
    }

    val bestText = "Best: ${highScoreManager.getHighestScore()}"
    val bestStyle = TextStyle(color = Color(0xFF888888), fontSize = 18.sp)
    val measuredBest = textMeasurer.measure(bestText, bestStyle)
    drawText(
        textLayoutResult = measuredBest,
        topLeft = Offset(size.width / 2 - measuredBest.size.width / 2, size.height / 2 + 90f)
    )

    if ((currentTime / 500) % 2 == 0L) {
        val retryText = "Click to retry"
        val retryStyle = TextStyle(color = Color.White, fontSize = 20.sp)
        val measuredRetry = textMeasurer.measure(retryText, retryStyle)
        drawText(
            textLayoutResult = measuredRetry,
            topLeft = Offset(size.width / 2 - measuredRetry.size.width / 2, size.height * 0.65f)
        )
    }
}
