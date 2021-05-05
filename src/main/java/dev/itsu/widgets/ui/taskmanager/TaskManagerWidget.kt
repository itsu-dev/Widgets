package dev.itsu.widgets.ui.taskmanager

import dev.itsu.widgets.ui.AbstractWidget
import javafx.application.Platform
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.chart.AreaChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.shape.Rectangle
import javafx.util.StringConverter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.management.ManagementFactory
import kotlin.math.round

@ExperimentalStdlibApi
class TaskManagerWidget : AbstractWidget() {

    private val root = HBox()

    private val cpuXAxis = NumberAxis(0.0, 10.0, 1.0)
    private val cpuYAxis = NumberAxis(0.0, 100.0, 25.0)
    private val cpuChart = AreaChart(cpuXAxis, cpuYAxis)
    private val cpuLabel = Label("CPU")
    private val cpuSeries = XYChart.Series<Number, Number>()

    private val totalMemory =
        (ManagementFactory.getOperatingSystemMXBean() as com.sun.management.OperatingSystemMXBean).totalPhysicalMemorySize / 1024.0 / 1024.0 / 1024.0
    private val memoryXAxis = NumberAxis(0.0, 10.0, 1.0)
    private val memoryYAxis = NumberAxis(0.0, totalMemory, totalMemory / 4.0)
    private val memoryChart = AreaChart(memoryXAxis, memoryYAxis)
    private val memoryLabel = Label("Memory")
    private val memorySeries = XYChart.Series<Number, Number>()

    private var time = 0

    init {
        setCSSId("task-manager-widget")
        setTitle("TaskManager")
        setIcon(TaskManagerWidget::class.java.classLoader.getResourceAsStream("icon/task_manager.png")!!)
        setContent(root)
        useSettingsButton(true)

        cpuChart.prefWidth = WIDGET_WIDTH / 2.0
        cpuChart.prefHeight = WIDGET_MIN_HEIGHT - TITLE_BAR_HEIGHT - 16
        cpuChart.styleClass.add("task-manager-chart")
        cpuChart.data.add(cpuSeries)

        cpuLabel.styleClass.add("widget-label")
        cpuLabel.id = "task-manager-widget-cpu-label"

        cpuSeries.node.lookup(".chart-series-area-line").styleClass.add("cpu-chart-series-area-line")
        cpuSeries.node.lookup(".chart-series-area-fill").styleClass.add("cpu-chart-series-area-fill")

        cpuXAxis.isTickLabelsVisible = false

        memoryChart.prefWidth = WIDGET_WIDTH / 2.0
        memoryChart.prefHeight = WIDGET_MIN_HEIGHT - TITLE_BAR_HEIGHT - 16
        memoryChart.styleClass.add("task-manager-chart")
        memoryChart.data.add(memorySeries)

        memoryLabel.styleClass.add("widget-label")
        memoryLabel.id = "task-manager-widget-memory-label"

        memorySeries.node.lookup(".chart-series-area-line").styleClass.add("memory-chart-series-area-line")
        memorySeries.node.lookup(".chart-series-area-fill").styleClass.add("memory-chart-series-area-fill")

        memoryXAxis.isTickLabelsVisible = false
        memoryYAxis.tickLabelFormatter = object : StringConverter<Number>() {
            override fun toString(d: Number): String {
                return (round(d.toDouble() * 10) / 10).toString()
            }

            override fun fromString(string: String): Number {
                return string.toDouble()
            }
        }

        root.children.addAll(
            VBox(
                cpuLabel,
                cpuChart
            ).also {
                it.alignment = Pos.CENTER_LEFT
                it.padding = Insets(8.0, 0.0, 0.0, 0.0)
            },
            VBox(
                memoryLabel,
                memoryChart
            ).also {
                it.alignment = Pos.CENTER_LEFT
                it.padding = Insets(8.0, 0.0, 0.0, 0.0)
            }
        )
        root.alignment = Pos.CENTER

        run()
    }

    private fun run() = GlobalScope.launch {
        while(true) {
            computeMemory()
            computeCPU()
            delay(1000)
            time++
        }
    }

    private fun computeMemory() {
        val usingMemory = totalMemory - ((ManagementFactory.getOperatingSystemMXBean() as com.sun.management.OperatingSystemMXBean).freePhysicalMemorySize / 1024.0 / 1024.0 / 1024.0)
        if (memorySeries.data.size > 10) {
            memorySeries.data.removeFirst()
        }
        Platform.runLater {
            if (time > 10) {
                (memoryChart.xAxis as NumberAxis).upperBound = time.toDouble()
                (memoryChart.xAxis as NumberAxis).lowerBound = time - 10.0
            }
            memoryLabel.text = "Memory (${round(usingMemory * 10) / 10} / ${round(totalMemory * 10) / 10}GB)"
            memorySeries.data.add(
                XYChart.Data(time as Number, usingMemory as Number).also {
                    it.node = Rectangle(0.0, 0.0).also {
                        it.isVisible = false
                    }
                }
            )
        }
    }

    private fun computeCPU() {
        val cpuRate = ((ManagementFactory.getOperatingSystemMXBean()) as com.sun.management.OperatingSystemMXBean).systemCpuLoad
        if (cpuSeries.data.size > 10) {
            cpuSeries.data.removeFirst()
        }
        Platform.runLater {
            if (time > 10) {
                (cpuChart.xAxis as NumberAxis).upperBound = time.toDouble()
                (cpuChart.xAxis as NumberAxis).lowerBound = time - 10.0
            }
            cpuLabel.text = "CPU (${round(cpuRate * 1000) / 10}%)"
            cpuSeries.data.add(
                XYChart.Data(time as Number, (cpuRate * 100) as Number).also {
                    it.node = Rectangle(0.0, 0.0).also {
                        it.isVisible = false
                    }
                }
            )
        }
    }

}