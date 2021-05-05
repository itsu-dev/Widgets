package dev.itsu.widgets

import dev.itsu.widgets.ui.googlesearch.GoogleSearchWidget
import dev.itsu.widgets.ui.links.LinksWidget
import dev.itsu.widgets.ui.taskmanager.TaskManagerWidget
import javafx.application.Platform
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.control.MenuItem
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.system.exitProcess

@ExperimentalStdlibApi
object UIManager {

    private lateinit var scene: Scene
    private lateinit var baseVBox: VBox
    private val clockLabel = Label("00:00")
    private val dateLabel = Label("0000年00月00日　-曜日")

    private var hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    private var minute = Calendar.getInstance().get(Calendar.MINUTE)
    private var year = Calendar.getInstance().get(Calendar.YEAR)
    private var month = Calendar.getInstance().get(Calendar.MONTH)
    private var day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    private var week = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)

    private val weeks = mapOf(1 to "日", 2 to "月", 3 to "火", 4 to "水", 5 to "木", 6 to "金", 7 to "土")

    fun create(scene: Scene) {
        this.scene = scene
        this.scene.stylesheets.add(Main::class.java.classLoader.getResource("core.css")?.toString())
        this.scene.fill = Color.TRANSPARENT

        this.baseVBox = this.scene.root as VBox
        this.baseVBox.padding = Insets(Environment.padding, Environment.padding, Environment.padding, Environment.padding)
        this.baseVBox.spacing = 16.0

        baseVBox.id = "base-vbox"
        clockLabel.id = "clock-label"
        dateLabel.id = "date-label"

        baseVBox.children.addAll(
            VBox(clockLabel, dateLabel),
            LinksWidget(),
            TaskManagerWidget(),
            GoogleSearchWidget()
            // ForecastWidget()
        )
    }

    fun startClock() {
        GlobalScope.launch {
            while (true) {
                val c = Calendar.getInstance()
                if (hour != c.get(Calendar.HOUR_OF_DAY)
                    || minute != c.get(Calendar.HOUR_OF_DAY)
                    || hour != c.get(Calendar.HOUR_OF_DAY)
                    || minute != c.get(Calendar.MINUTE)
                    || year != c.get(Calendar.YEAR)
                    || month != c.get(Calendar.MONTH)
                    || day != c.get(Calendar.DAY_OF_MONTH)
                    || week != c.get(Calendar.DAY_OF_WEEK)) {

                    hour = c.get(Calendar.HOUR_OF_DAY)
                    minute = c.get(Calendar.MINUTE)
                    year = c.get(Calendar.YEAR)
                    month = c.get(Calendar.MONTH)
                    day = c.get(Calendar.DAY_OF_MONTH)
                    week = c.get(Calendar.DAY_OF_WEEK)
                    Platform.runLater {
                        clockLabel.text = "$hour:${if (minute < 10) "0" else ""}$minute"
                        dateLabel.text = "${year}年${month + 1}月${day}日　${weeks[week]}曜日"
                    }
                }
                delay(3000)
            }
        }
    }

}