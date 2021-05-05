package dev.itsu.widgets.ui

import dev.itsu.widgets.Environment
import dev.itsu.widgets.UIManager
import dev.itsu.widgets.ui.links.LinksWidget
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.control.MenuItem
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import java.io.InputStream
import kotlin.system.exitProcess

abstract class AbstractWidget : Parent() {

    private val baseBox = VBox()
    private val titleBarBox = HBox()
    private val titleLabel = Label()
    private val icon = ImageView()

    private var onSettingsButtonClicked: () -> Unit = {}

    companion object {
        const val TITLE_BAR_HEIGHT = 32.0
        const val WIDGET_MIN_HEIGHT = 156.0

        val WIDGET_WIDTH = Environment.width - (32.0 + Environment.padding * 2)
    }

    init {
        this.styleClass.add("base-widget")
        this.minHeight(WIDGET_MIN_HEIGHT)
        this.prefWidth(Environment.width - Environment.padding * 2)

        titleLabel.id = "widget-title-label"

        icon.fitHeight = 16.0
        icon.fitWidth = 16.0

        // https://stackoverflow.com/questions/12118681/how-can-i-create-resizing-spacers-in-javafx
        val spacer = Region()
        HBox.setHgrow(spacer, Priority.ALWAYS)
        spacer.minWidth = Region.USE_PREF_SIZE

        titleBarBox.id = "widget-title-bar"
        titleBarBox.spacing = 16.0
        titleBarBox.prefHeight = TITLE_BAR_HEIGHT
        titleBarBox.prefWidth = Environment.width - Environment.padding * 2
        titleBarBox.alignment = Pos.CENTER_LEFT
        titleBarBox.setOnMouseClicked {
            val menu = ContextMenu()
            menu.items.add(
                MenuItem("終了").also {
                    it.setOnAction {
                        exitProcess(0)
                    }
                }
            )
            menu.show(titleBarBox, it.screenX, it.screenY)
        }
        titleBarBox.children.addAll(icon, titleLabel, spacer)

        baseBox.prefWidth = Environment.width - Environment.padding * 2
        baseBox.styleClass.add("widget")
        baseBox.children.add(titleBarBox)

        this.children.add(baseBox)
    }

    fun setCSSId(id: String) {
        baseBox.id = id
    }

    fun setTitle(title: String) {
        titleLabel.text = title
    }

    fun setIcon(inputStream: InputStream) {
        icon.image = Image(inputStream)
    }

    fun addToTitleBox(node: Node) {
        titleBarBox.children.add(node)
    }

    fun setContent(node: Node) {
        node.minHeight(WIDGET_MIN_HEIGHT - TITLE_BAR_HEIGHT)

        if (node is Pane) {
            node.padding = Insets(0.0, 16.0, 0.0, 16.0)
            if (node.prefHeight == -1.0) node.prefHeight = WIDGET_MIN_HEIGHT - TITLE_BAR_HEIGHT
        }

        this.baseBox.children.add(node)
    }

    fun useSettingsButton(boolean: Boolean) {
        if (boolean) {
            this.addToTitleBox(
                Button().also {
                    it.styleClass.add("widget-button")
                    it.graphic =
                        ImageView(Image(LinksWidget::class.java.classLoader.getResourceAsStream("icon/settings.png")!!)).also {
                            it.fitWidth = 16.0
                            it.fitHeight = 16.0
                        }
                    it.setOnAction {
                        onSettingsButtonClicked.invoke()
                    }
                }
            )
        }
    }

    fun setOnSettingsButtonClicked(func: () -> Unit) {
        onSettingsButtonClicked = func
    }

}