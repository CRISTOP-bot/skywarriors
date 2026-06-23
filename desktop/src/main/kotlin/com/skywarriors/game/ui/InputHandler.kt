package com.skywarriors.game.ui

data class InputState(
    val isDown: Boolean = false,
    val x: Float = 0f,
    val y: Float = 0f
)

class InputHandler {
    private var isDown: Boolean = false
    private var x: Float = 0f
    private var y: Float = 0f

    fun onMouseDown(mx: Float, my: Float) {
        isDown = true
        x = mx
        y = my
    }

    fun onMouseUp() {
        isDown = false
    }

    fun onMouseMove(mx: Float, my: Float) {
        if (isDown) {
            x = mx
            y = my
        }
    }

    fun getInput(): InputState = InputState(isDown, x, y)

    fun reset() {
        isDown = false
        x = 0f
        y = 0f
    }
}
