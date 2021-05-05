package dev.itsu.widgets.ui.forecast

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.PNGTranscoder
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

class ForecastAPI(private val areaId: String) {

    private var onPreLoad: () -> Unit = {}
    private var onSuccess: (result: String) -> Unit = {}
    private var onError: (e: Exception) -> Unit = {}

    companion object {
        private const val FORECAST_URL = "https://www.jma.go.jp/bosai/forecast/data/forecast/%s.json"
        private const val FORECAST_OVERVIEW_URL = "https://www.jma.go.jp/bosai/forecast/data/overview_forecast/%s.json"
        private const val IMAGE_URL = "https://www.jma.go.jp/bosai/forecast/img/%s.svg"

        fun getImage(id: String): Pair<String, Boolean> {
            try {
                File("./forecast/icons").mkdirs()

                var result = ""
                val c = URL(String.format(IMAGE_URL, id)).openConnection() as HttpURLConnection
                c.requestMethod = "GET"
                c.inputStream.bufferedReader(StandardCharsets.UTF_8).use {
                    result = it.readText()
                    it.close()
                }
                println(result)

                val input = TranscoderInput(result.reader())
                val t = PNGTranscoder()
                val output = TranscoderOutput(FileOutputStream(File("./forecast/icons/${id}.png")))
                t.addTranscodingHint(PNGTranscoder.KEY_WIDTH, 64.0F)
                t.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, 64.0F)
                t.transcode(input, output)
                return id to true

            } catch (e: Exception) {
                e.printStackTrace()
                return id to false
            }
        }
    }

    fun loadOverview() {
        load(String.format(FORECAST_OVERVIEW_URL, areaId))
    }

    fun loadForecast() = GlobalScope.launch {
        load(String.format(FORECAST_URL, areaId))
    }

    private fun load(url: String) = GlobalScope.launch {
        onPreLoad.invoke()

        try {
            var result = ""
            val c = URL(url).openConnection() as HttpURLConnection
            c.requestMethod = "GET"
            c.inputStream.bufferedReader(StandardCharsets.UTF_8).use {
                result = it.readText()
                it.close()
            }
            c.disconnect()
            onSuccess.invoke(result)
        } catch (e: Exception) {
            onError.invoke(e)
        }
    }

    fun setOnPreLoad(func: () -> Unit): ForecastAPI {
        this.onPreLoad = func
        return this
    }

    fun setOnSuccess(func: (result: String) -> Unit): ForecastAPI {
        this.onSuccess = func
        return this
    }

    fun setOnError(func: (e: Exception) -> Unit): ForecastAPI {
        this.onError = func
        return this
    }
}