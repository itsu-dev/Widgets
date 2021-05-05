package dev.itsu.widgets.ui.forecast

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.itsu.widgets.ui.AbstractWidget
import dev.itsu.widgets.ui.links.LinksWidget
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

class ForecastWidget : AbstractWidget() {

    private val root = HBox()
    private val weatherIcon = ImageView()
    private val overViewLabel = Label()

    init {
        setCSSId("forecast-widget")
        setTitle("Forecast powered by 気象庁")
        setIcon(LinksWidget::class.java.classLoader.getResourceAsStream("icon/forecast.png")!!)
        setContent(root)

        addToTitleBox(
            Button().also {
                it.styleClass.add("widget-button")
                it.graphic =
                    ImageView(Image(ForecastWidget::class.java.classLoader.getResourceAsStream("icon/reload.png")!!)).also {
                        it.fitWidth = 16.0
                        it.fitHeight = 16.0
                    }
                it.setOnAction { this.loadData() }
            }
        )

        useSettingsButton(true)

        weatherIcon.fitWidth = 64.0
        weatherIcon.fitHeight = 64.0

        overViewLabel.styleClass.add("widget-label")
        overViewLabel.isWrapText = true

        root.spacing = 16.0
        root.alignment = Pos.CENTER_LEFT
        root.children.addAll(weatherIcon, overViewLabel)

        loadData()
    }

    private fun loadData() {
        ForecastAPI("090000")
            .setOnSuccess { onForecastLoaded(it) }
            .setOnError { onError(it) }
            .loadForecast()

        ForecastAPI("090000")
            .setOnSuccess { onOverviewLoaded(it) }
            .setOnError { onError(it) }
            .loadOverview()

        GlobalScope.launch {
            if (!File("./forecast/icons/102.png").exists())
                onImageLoaded(ForecastAPI.getImage("102"))
            else onImageLoaded("102" to true)
        }
    }

    private fun onForecastLoaded(json: String) = Platform.runLater {

    }

    private fun onOverviewLoaded(json: String) = Platform.runLater {
        val data = Gson().fromJson<Map<String, String>>(json, object : TypeToken<Map<String, String>>(){}.type)
        overViewLabel.text = data["headlineText"]
    }

    private fun onImageLoaded(result: Pair<String, Boolean>) {
        var imageInput: InputStream?
        if (!result.second) {
            imageInput = ForecastWidget::class.java.classLoader.getResourceAsStream("./icon/forecast/${result.first}.png")
            imageInput ?: run {
                imageInput = ForecastWidget::class.java.classLoader.getResourceAsStream("icon/icon_not_found.png")
            }
        } else {
            imageInput = FileInputStream(File("./forecast/icons/${result.first}.png"))
        }
        Platform.runLater {
            weatherIcon.image = Image(imageInput)
        }
    }

    private fun onError(e: Exception) = Platform.runLater {
        root.alignment = Pos.CENTER
        root.children.add(
            Label("Error（${e.javaClass.simpleName}）").also {
                it.styleClass.add("widget-label")
            }
        )
    }

}