package com.example.accelerometer
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.accelerometer.R

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private lateinit var acceleratorDao: AcceleratorDao
    private lateinit var angle1TextView: TextView
    private lateinit var angle2TextView: TextView
    private lateinit var angle3TextView: TextView

    private val handler = Handler()
    private val dataIntervalMillis = 1000L // Interval for updating the graph (1 second)
    private var rotationStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val acceleratorDatabase = AcceleratorDatabase.DatabaseFactory.getInstance(applicationContext)
        acceleratorDao = acceleratorDatabase.acceleratorDao()

        angle1TextView = findViewById(R.id.Angle1)
        angle2TextView = findViewById(R.id.Angle2)
        angle3TextView = findViewById(R.id.Angle3)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also { accelerometer ->
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
            lastRecordedTimestamp = System.currentTimeMillis()
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    private val dataRecordingIntervalMillis = 1000L // Interval for recording data (1 second)
    private var lastRecordedTimestamp = 0L

    private var dataRecordedThisSecond = false

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val g = event.values.clone()

            val norm = Math.sqrt((g[0] * g[0] + g[1] * g[1] + g[2] * g[2]).toDouble()).toFloat()
            g[0] /= norm
            g[1] /= norm
            g[2] /= norm

            val angle1 = Math.toDegrees(Math.acos(g[0].toDouble())).toFloat()
            val angle2 = Math.toDegrees(Math.acos(g[1].toDouble())).toFloat()
            val angle3 = Math.toDegrees(Math.acos(g[2].toDouble())).toFloat()

            angle1TextView.text = "Angle 1: $angle1"
            angle2TextView.text = "Angle 2: $angle2"
            angle3TextView.text = "Angle 3: $angle3"

            val currentTimeMillis = System.currentTimeMillis()
            if (currentTimeMillis - lastRecordedTimestamp >= dataRecordingIntervalMillis && !dataRecordedThisSecond) {
                // Record data every second
                val accelerator = Accelerator(0, angle1, angle2, angle3, currentTimeMillis)

                Thread {
                    acceleratorDao.insert(accelerator)
                }.start()

                lastRecordedTimestamp = currentTimeMillis
                dataRecordedThisSecond = true

                // Reset the flag after one second
                handler.postDelayed({
                    dataRecordedThisSecond = false
                }, dataRecordingIntervalMillis)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    fun showGraphActivity(view: View) {
        val intent = Intent(this, ForGraphs::class.java)
        startActivity(intent)
    }
}
