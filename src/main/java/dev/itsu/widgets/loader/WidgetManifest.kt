package dev.itsu.widgets.loader

data class WidgetManifest(
    val packageName: String,
    val version: String,
    val author: String,
    val description: String,
    val widgets: List<Widget>
) {
    data class Widget(
        val widgetName: String,
        val mainClass: String,
        val title: String,
        val iconPath: String
    )
}