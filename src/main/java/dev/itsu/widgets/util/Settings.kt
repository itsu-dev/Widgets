package dev.itsu.widgets.util

import org.dom4j.io.SAXReader
import org.redundent.kotlin.xml.PrintOptions
import org.redundent.kotlin.xml.xml
import java.io.File
import java.nio.charset.StandardCharsets

object Settings {

    var position = "left"
    var widgets = mutableListOf<Pair<String, String>>()

    fun createSettingsFile() {
        val file = File("./settings/settings.xml")
        if (file.exists()) return

        val settings = xml("settings") {
            "position" { -"left" }
            "widgets" {
                "widget" {
                    "file" { -"default_widgets.jar" }
                    "mainClass" { -"dev.itsu.widgets.ui.links.LinksWidget" }
                }
                "widget" {
                    "file" { -"default_widgets.jar" }
                    "mainClass" { -"dev.itsu.widgets.ui.taskmanager.TaskManagerWidget" }
                }
                "widget" {
                    "file" { -"default_widgets.jar" }
                    "mainClass" { -"dev.itsu.widgets.ui.googlesearch.GoogleSearchWidget" }
                }
            }
        }

        store(settings.toString(PrintOptions(true, true, false)))
    }

    fun loadSettingsFile() {
        val file = File("./settings/settings.xml")
        if (!file.exists()) createSettingsFile()

        val document = SAXReader().read(file).rootElement
        position = document.selectSingleNode("position").text

        document.selectSingleNode("widgets").selectNodes("widget").forEach {
            widgets.add(Pair(
                it.selectSingleNode("file").text, it.selectSingleNode("mainClass").text
            ))
        }
    }

    private fun store(content: String) {
        File("./settings/settings.xml").bufferedWriter(StandardCharsets.UTF_8).use {
            it.write(content)
            it.flush()
        }
    }
}