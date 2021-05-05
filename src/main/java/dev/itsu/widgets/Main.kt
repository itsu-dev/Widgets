package dev.itsu.widgets

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.VBox
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.awt.GraphicsEnvironment

@ExperimentalStdlibApi
class Main : Application() {

    override fun start(primaryStage: Stage) {
        primaryStage.isResizable = false
        primaryStage.isAlwaysOnTop = false
        primaryStage.x = 0.0
        primaryStage.y = 0.0
        primaryStage.width = GraphicsEnvironment.getLocalGraphicsEnvironment().maximumWindowBounds.getWidth() / 3.0
        primaryStage.height = GraphicsEnvironment.getLocalGraphicsEnvironment().maximumWindowBounds.getHeight()
        primaryStage.initStyle(StageStyle.UTILITY)
        primaryStage.opacity = 0.0

        val secondaryStage = Stage()
        secondaryStage.isResizable = false
        secondaryStage.isAlwaysOnTop = false
        secondaryStage.x = 0.0
        secondaryStage.y = 0.0
        secondaryStage.width = Environment.width
        secondaryStage.height = Environment.height
        secondaryStage.initStyle(StageStyle.TRANSPARENT)
        secondaryStage.initOwner(primaryStage)

        val scene = Scene(VBox(), secondaryStage.width, secondaryStage.height)
        secondaryStage.scene = scene

        UIManager.create(scene)
        UIManager.startClock()

        primaryStage.toBack()
        primaryStage.show()
        secondaryStage.show()
    }

}

@ExperimentalStdlibApi
fun main(args: Array<String>) {
    Application.launch(Main::class.java, *args)
}