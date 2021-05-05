package dev.itsu.widgets.ui.links

import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

class LinksSettingWindow(private val widget: LinksWidget) {

    private val stage = Stage()
    private val name = TextField()
    private val url = TextField()
    private val icon = TextField()

    private val list = ListView<Label>()
    private val scene: Scene

    private var data = mutableListOf<Link>()

    init {
        list.setPrefSize(400.0, 500.0)

        name.promptText = "名前"
        name.prefWidth = 300.0

        url.promptText = "URL"
        url.prefWidth = 300.0

        icon.promptText = "アイコンのパス"
        icon.prefWidth = 300.0

        val vBox = VBox()
        vBox.alignment = Pos.CENTER
        vBox.spacing = 8.0
        vBox.prefWidth = 400.0
        vBox.children.addAll(
            Label("新規"), name, url, icon,
            Button("追加").also {
                it.setOnAction {
                    data.add(Link(name.text, url.text, icon.text))
                    GlobalScope.launch {
                        val icon = File(icon.text)
                        if (icon.exists()) icon.copyTo(File("./links/icons/${name.text}.icon"), true)
                        LinksUtil.updateData(data)
                    }
                    updateList()
                }
            }
        )

        scene = Scene(HBox(list, vBox), 800.0, 500.0)

        GlobalScope.launch {
            data = LinksUtil.readData()
            updateList()
        }

        stage.width = 800.0
        stage.height = 500.0
        stage.isResizable = false
        stage.scene = scene
        stage.title = "設定 - Links"

        stage.setOnCloseRequest {
            widget.onSettingWindowClosed()
        }

        stage.show()
    }

    private fun updateList() {
        list.items.clear()
        data.forEach { link ->
            Platform.runLater {
                list.items.add(
                    Label("${link.name} / ${link.url}").also {
                        val icon = File("./links/icons/${link.name}.icon")
                        it.graphic = ImageView(
                            Image(
                                if (icon.exists()) icon.inputStream() else LinksSettingWindow::class.java.classLoader.getResourceAsStream("icon/links_32.png")!!
                            )
                        ).also {
                            it.fitWidth = 32.0
                            it.fitHeight = 32.0
                        }
                    }
                )
            }
        }
    }

}