package dev.itsu.widgets.ui.googlesearch

import dev.itsu.widgets.ui.AbstractWidget
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import java.awt.Desktop
import java.net.URI

class GoogleSearchWidget : AbstractWidget() {

    private val root = HBox()

    private val googleSearchField = TextField()
    private val searchButton = Button()

    private val buttonImage = ImageView(Image(GoogleSearchWidget::class.java.classLoader.getResourceAsStream("icon/google_search_button.png")!!))
    private val buttonImageHovered = ImageView(Image(GoogleSearchWidget::class.java.classLoader.getResourceAsStream("icon/google_search_button_hovered.png")!!))

    init {
        setCSSId("google-search-widget")
        setTitle("Google検索")
        setIcon(GoogleSearchWidget::class.java.classLoader.getResourceAsStream("icon/google_search.png")!!)
        setContent(root)
        useSettingsButton(true)

        searchButton.id = "google-search-button"
        searchButton.graphic = buttonImage
        searchButton.setOnMouseEntered {
            searchButton.graphic = buttonImageHovered
        }
        searchButton.setOnMouseExited {
            searchButton.graphic = buttonImage
        }
        searchButton.setOnAction {
            if (googleSearchField.text.isNotEmpty()) {
                Desktop.getDesktop().browse(
                    URI("https://www.google.com/search?q=%s&ie=UTF-8".format(googleSearchField.text.replace(" ", "+"))))
            }
        }

        googleSearchField.styleClass.add("widget-text-field")
        googleSearchField.prefWidth = WIDGET_WIDTH - 96

        root.alignment = Pos.CENTER
        root.children.addAll(googleSearchField, searchButton)
    }

}