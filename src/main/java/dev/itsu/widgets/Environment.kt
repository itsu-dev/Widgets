package dev.itsu.widgets

import java.awt.GraphicsEnvironment

object Environment {

    val width = GraphicsEnvironment.getLocalGraphicsEnvironment().maximumWindowBounds.getWidth() / 3.0
    val height = GraphicsEnvironment.getLocalGraphicsEnvironment().maximumWindowBounds.getHeight()
    const val padding = 32.0

}