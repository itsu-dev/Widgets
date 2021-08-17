package dev.itsu.widgets.ui.manaba

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.itsu.manaba.Manaba
import dev.itsu.widgets.ui.AbstractWidget
import javafx.application.Platform
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.control.Label
import javafx.scene.control.ScrollBar
import javafx.scene.control.ScrollPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import model.Assignment
import java.awt.Desktop
import java.awt.Toolkit
import java.net.URI
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*

class ManabaAssignmentsWidget : AbstractWidget() {

    private val root = VBox()
    private val pane = ScrollPane(root)

    init {
        setCSSId("manaba-assignments-widget")
        setTitle("manaba 未提出課題")
        setIcon(ManabaAssignmentsWidget::class.java.classLoader.getResourceAsStream("icon/manaba-assignments.png")!!)
        setContent(pane)
        useSettingsButton(true)

        root.padding = Insets(16.0, 16.0, 16.0, 16.0)
        root.spacing = 3.0

        pane.maxHeight = 32.0 * 3 + 3.0 * 3 + root.padding.bottom + root.padding.top
        pane.isFitToWidth = true
        pane.style = "-fx-background-color: transparent;"

        reload()
    }

    private fun reload() {
        setText("ログインしています...")

        GlobalScope.launch {
            try {
                val credentials = Gson().fromJson<Map<String, String>>(
                    ManabaAssignmentsWidget::class.java.classLoader.getResourceAsStream("credentials.json").bufferedReader(StandardCharsets.UTF_8),
                    object : TypeToken<Map<String, String>>(){}.type
                )
                if (!Manaba.login(credentials["username"] ?: "", credentials["password"] ?: "")) {
                    setText("ログインに失敗しました（IDまたはパスワードが違います）")
                    return@launch
                }

            } catch (e: Exception) {
                setText("ログインに失敗しました（${e::class.java.simpleName}）")
                return@launch
            }

            try {
                setText("情報を取得しています...")

                val format = SimpleDateFormat("yyyy/MM/dd HH:mm")
                val now = System.currentTimeMillis()
                val assignments = Manaba.getAssignments()

                Platform.runLater {
                    root.children.clear()

                    assignments.forEach {
                        val box = HBox()
                        box.id = "manaba-assignment-cell"
                        box.prefWidth = root.width - (root.padding.right + root.padding.left)
                        box.cursor = Cursor.HAND
                        box.setOnMouseClicked { _ -> Desktop.getDesktop().browse(URI.create(it.url)) }

                        val nameLabel = Label(it.title)
                        nameLabel.id = "manaba-assignment-cell-title"
                        nameLabel.prefWidth = box.prefWidth / 2.0
                        nameLabel.maxWidth = nameLabel.prefWidth
                        nameLabel.prefHeight = 32.0
                        nameLabel.minHeight = 32.0
                        nameLabel.maxHeight = 32.0

                        (it.expiredAt - now).run {
                            val color = when {
                                this < 0 -> "#bdbdbd"
                                this <= 86400000 -> "#ef5350" // red
                                this <= 86400000 * 3 -> "#ffee58" // yellow
                                this <= 86400000 * 7 -> "#9ccc65" // green
                                else -> "#bdbdbd"
                            }
                            nameLabel.style = "-fx-border-color: $color;"
                            if (color != "#bdbdbd") box.style = "-fx-background-color: ${color}4D;"
                        }

                        val expiredAtLabel = Label(format.format(Date(it.expiredAt)))
                        expiredAtLabel.id = "manaba-assignment-cell-expired"
                        expiredAtLabel.prefWidth = box.prefWidth / 2.0
                        expiredAtLabel.maxWidth = nameLabel.prefWidth
                        expiredAtLabel.prefHeight = 32.0
                        expiredAtLabel.minHeight = 32.0
                        expiredAtLabel.maxHeight = 32.0
                        expiredAtLabel.alignment = Pos.CENTER_RIGHT
                        expiredAtLabel.padding = Insets(0.0, 16.0, 0.0, 0.0)

                        box.children.addAll(nameLabel, expiredAtLabel)

                        root.children.add(box)
                    }
                }

            } catch (e: Exception) {
                setText("取得に失敗しました（${e::class.java.simpleName}）")
                return@launch
            }
        }
    }

    private fun setText(text: String) = Platform.runLater {
        root.children.clear()
        root.children.add(Label(text))
    }
}