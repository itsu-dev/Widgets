package dev.itsu.widgets

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.nio.charset.StandardCharsets

class Parser {
    init {
        val data = Gson().fromJson<Map<String, Map<String, Any>>>(
            Parser::class.java.classLoader.getResourceAsStream("weather_forecast/area_codes_raw.json").buffered().reader(StandardCharsets.UTF_8),
            object : TypeToken<Map<String, Map<String, Any>>>(){}.type
        )
        val newData = mutableMapOf<String, String>()
        data.forEach { key, value ->
            newData[value["name"].toString()] = key
        }
        File("area_codes.json").writer(StandardCharsets.UTF_8).use {
            it.write(Gson().toJson(newData))
            it.close()
        }
    }
}

fun main() {
    //Parser()
}