package dev.itsu.widgets.ui.links

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.nio.charset.StandardCharsets

object LinksUtil {

    fun readData(): MutableList<Link> {
        val file = File("./links/links.json")
        if (file.exists())
            return Gson().fromJson(File("./links/links.json").bufferedReader(StandardCharsets.UTF_8), object : TypeToken<MutableList<Link>>(){}.type)
        else
            file.bufferedWriter(StandardCharsets.UTF_8).use {
                it.write(Gson().toJson(mutableListOf<Link>()))
                it.close()
            }
        return mutableListOf()
    }

    fun updateData(data: MutableList<Link>) {
        val json = Gson().toJson(data)
        File("./links/links.json").bufferedWriter(StandardCharsets.UTF_8).use {
            it.write(json)
            it.close()
        }
    }

}