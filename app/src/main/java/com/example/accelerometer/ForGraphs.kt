package com.example.accelerometer

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ForGraphs : AppCompatActivity() {
    private lateinit var db: AcceleratorDatabase
    private lateinit var dao: AcceleratorDao

    private lateinit var chart1: LineChart
    private lateinit var chart2: LineChart
    private lateinit var chart3: LineChart
    private val handler = Handler(Looper.getMainLooper())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_for_graphs)

        // Initialize the database and DAO
        db = AcceleratorDatabase.DatabaseFactory.getInstance(this)
        dao = db.acceleratorDao()

        // Get references to the LineChart views
        chart1 = findViewById(R.id.chart1)
        chart2 = findViewById(R.id.chart2)
        chart3 = findViewById(R.id.chart3)
    }

    override fun onResume() {
        super.onResume()

        // Launch a coroutine to collect data from the database
        CoroutineScope(Dispatchers.IO).launch {
            dao.getAllAccelerators().collectLatest { accelerators ->
                // Convert the data to a format that can be displayed in the charts
                val entries1 = accelerators.map { Entry(it.timestamp.toFloat(), it.x) }
                val entries2 = accelerators.map { Entry(it.timestamp.toFloat(), it.y) }
                val entries3 = accelerators.map { Entry(it.timestamp.toFloat(), it.z) }

                // Create a LineDataSet for each set of data
                val dataSet1 = LineDataSet(entries1, "X").apply {
                    lineWidth = 2f
                    color = Color.RED
                    setCircleColor(Color.RED)
                    setDrawValues(false)
                    setDrawCircles(false)
                    setDrawFilled(true)
                    fillColor = Color.RED
                }

                val dataSet2 = LineDataSet(entries2, "Y").apply {
                    lineWidth = 2f
                    color = Color.GREEN
                    setCircleColor(Color.GREEN)
                    setDrawValues(false)
                    setDrawCircles(false)
                    setDrawFilled(true)
                    fillColor = Color.GREEN
                }

                val dataSet3 = LineDataSet(entries3, "Z").apply {
                    lineWidth = 2f
                    color = Color.BLUE
                    setCircleColor(Color.BLUE)
                    setDrawValues(false)
                    setDrawCircles(false)
                    setDrawFilled(true)
                    fillColor = Color.BLUE
                }

                // Update the charts on the UI thread
                withContext(Dispatchers.Main) {
                    chart1.data = LineData(dataSet1)
                    chart2.data = LineData(dataSet2)
                    chart3.data = LineData(dataSet3)

                    // Remove the oldest entry when a new entry is added
                    if (chart1.data.dataSetCount > 0) {
                        val dataSet1 = chart1.data.getDataSetByIndex(0) as LineDataSet
                        val dataSet2 = chart2.data.getDataSetByIndex(0) as LineDataSet
                        val dataSet3 = chart3.data.getDataSetByIndex(0) as LineDataSet

                        if (dataSet1.entryCount > 100) {
                            dataSet1.removeFirst()
                            dataSet2.removeFirst()
                            dataSet3.removeFirst()
                        }
                    }

                    chart1.notifyDataSetChanged() // Let the chart know it's data changed
                    chart2.notifyDataSetChanged() // Let the chart know it's data changed
                    chart3.notifyDataSetChanged() // Let the chart know it's data changed

                    chart1.isDragEnabled = true
                    chart1.setScaleEnabled(true)
                    chart1.setPinchZoom(true)

                    chart2.isDragEnabled = true
                    chart2.setScaleEnabled(true)
                    chart2.setPinchZoom(true)

                    chart3.isDragEnabled = true
                    chart3.setScaleEnabled(true)
                    chart3.setPinchZoom(true)

                    chart1.invalidate() // Refresh the chart
                    chart2.invalidate() // Refresh the chart
                    chart3.invalidate() // Refresh the chart
                }
            }
            delay(1000)
        }
    }
}


