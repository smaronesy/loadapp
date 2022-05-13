package com.udacity

sealed class ButtonState(val state: String) {
    object Clicked : ButtonState("Clicked")
    object Loading : ButtonState("Loading")
    object Completed : ButtonState("Completed")

    fun next() = when (this) {
        Clicked -> Loading
        Loading -> Completed
        Completed -> Clicked
    } as Int
}