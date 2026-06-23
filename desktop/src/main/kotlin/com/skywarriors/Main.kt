package com.skywarriors

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.skywarriors.game.engine.GamePanel

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Sky Warriors",
        state = WindowState(size = DpSize(540.dp, 960.dp)),
        resizable = false
    ) {
        GamePanel()
    }
}
