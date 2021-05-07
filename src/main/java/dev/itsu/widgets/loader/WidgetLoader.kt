package dev.itsu.widgets.loader

import org.dom4j.Document
import org.dom4j.io.SAXReader
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.jar.JarFile

object WidgetLoader {

    fun loadWidgets(): List<WidgetManifest> {
        val file = File("./extensions")
        if (!file.exists()) file.mkdirs()

        val result = mutableListOf<WidgetManifest>()
        file.listFiles()?.forEach {
            if (!it.isDirectory && it.extension == "jar")
                result.add(loadWidget(it.absolutePath))
        }

        return result.toList()
    }

    fun loadWidget(path: String): WidgetManifest {
        val file = File(path)
        if (!file.exists()) throw FileNotFoundException("File ($path) does not exist!")
        if (file.isDirectory || file.extension != "jar") throw IllegalArgumentException("File ($path) must be *.jar file!")

        val jarFile = JarFile(file)
        val manifestInputStream =
            WidgetLoader::class.java.classLoader.getResourceAsStream(jarFile.getJarEntry("WidgetManifest.xml").name)

        manifestInputStream ?: run {
            throw FileNotFoundException("File ($path) does not content WidgetManifest.xml!")
        }

        return loadManifest(manifestInputStream)
    }

    private fun loadManifest(inputStream: InputStream): WidgetManifest {
        val document = SAXReader().read(inputStream).rootElement

        val packageName = document.selectSingleNode("PackageName").text
        val version = document.selectSingleNode("Version").text
        val author = document.selectSingleNode("Author").text
        val description = document.selectSingleNode("Description").text
        val widgets = mutableListOf<WidgetManifest.Widget>()

        val widgetsNodes = document.selectSingleNode("Widgets").selectNodes("Widget")
        widgetsNodes.forEach {
            widgets.add(
                WidgetManifest.Widget(
                    it.selectSingleNode("WidgetName").text,
                    it.selectSingleNode("MainClass").text,
                    it.selectSingleNode("Title").text,
                    it.selectSingleNode("IconPath").text
                )
            )
        }

        return WidgetManifest(
            packageName,
            version,
            author,
            description,
            widgets
        )
    }
}