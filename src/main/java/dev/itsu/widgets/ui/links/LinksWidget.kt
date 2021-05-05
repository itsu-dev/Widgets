package dev.itsu.widgets.ui.links

import dev.itsu.widgets.ui.AbstractWidget
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import java.awt.Desktop
import java.io.File
import java.net.URI

class LinksWidget : AbstractWidget() {

    private val root = HBox()

    init {
        File("./links/icons").mkdirs()

        setCSSId("links-widget")
        setTitle("Links")
        setIcon(LinksWidget::class.java.classLoader.getResourceAsStream("icon/links.png")!!)
        setContent(root)
        useSettingsButton(true)
        setOnSettingsButtonClicked {
            LinksSettingWindow(this)
        }

        root.alignment = Pos.CENTER_LEFT

        reloadData()
    }

    private fun reloadData() {
        root.children.clear()
        LinksUtil.readData().forEach { link ->
            root.children.add(
                Button(link.name).also {
                    val icon = File("./links/icons/${link.name}.icon")
                    it.graphic = ImageView(
                        Image(
                            if (icon.exists()) icon.inputStream() else LinksSettingWindow::class.java.classLoader.getResourceAsStream("icon/links_32.png")!!
                        )
                    ).also {
                        it.fitWidth = 64.0
                        it.fitHeight = 64.0
                    }
                    it.styleClass.add("widget-button")
                    it.graphicTextGap = 4.0
                    it.text = link.name
                    it.setOnAction {
                        Desktop.getDesktop().browse(URI(link.url))
                    }
                }
            )
        }
    }

    fun onSettingWindowClosed() {
        reloadData()
    }

}