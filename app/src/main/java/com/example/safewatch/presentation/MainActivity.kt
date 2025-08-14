/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.example.safewatch.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.tooling.preview.devices.WearDevices
import com.example.safewatch.R
import com.example.safewatch.presentation.theme.SafeWatchTheme
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.safewatch.Auth.InternalData
import com.example.safewatch.Auth.UserConfig
import com.example.safewatch.Auth.sendAlert
import com.example.safewatch.Auth.sendMessageToPhone
import kotlin.math.sqrt

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var config: UserConfig
    private lateinit var localStorage: InternalData

    val newConfig = UserConfig(
        "Francisco",
        "",
        180,
        50,
        false
    )

    private lateinit var sensorManager: SensorManager
    private lateinit var pulseTextView: TextView
    private lateinit var accelerometerTextView: TextView
    private var heartRateSensor: Sensor? = null
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null
    private var sensor:Sensor? = null
    var numTry: Int = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        localStorage = InternalData(this)

        val loadedConfig = localStorage.getData()
        config = if (loadedConfig != null) {
            loadedConfig
        } else {
            val defaultConfig = UserConfig(
                username = "Francisco",
                email = "ortizmedinajosefrancisco@gmail.com",
                maxBpm = 180,
                minBpm = 50,
                recAlert = false
            )
            localStorage.save(defaultConfig)
            defaultConfig
        }
        Log.d("Nose1", config.toString())
        // setTheme(android.R.style.Theme_DeviceDefault)

        setContentView(R.layout.main)

        pulseTextView = findViewById(R.id.Pulse)
        accelerometerTextView = findViewById(R.id.accelerometer)

        // Obtener el SensorManager y el sensor de pulso
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        val history: ImageButton = findViewById(R.id.history)
        val alert: ImageButton = findViewById(R.id.alert)

        startSensor()
        if (heartRateSensor == null) {
            Log.e("Sensor", "Sensor de pulso no disponible")
            pulseTextView.text = "No disponible"
        }

        history.setOnClickListener {
            //Toast.makeText(this,"Hola mundo", Toast.LENGTH_SHORT -10).show()
            val intent = Intent(this@MainActivity, History::class.java)
            startActivity(intent)
        }

        alert.setOnClickListener{
            if (config.recAlert) {
                sendAlert(config.username, config.email, "Alerta manual") { success, message ->
                    if (success) {
                        sendMessageToPhone("/alerta", "¡Pulso alto!", this)
                        runOnUiThread {
                            Toast.makeText(
                                this@MainActivity,
                                "Alerta enviada con exito",
                                Toast.LENGTH_SHORT - 10
                            ).show()
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                this@MainActivity,
                                "¡Ocurrio un error inesperado!",
                                Toast.LENGTH_SHORT - 10
                            ).show()
                        }
                    }
                }
            } else {
                val intent = Intent(this@MainActivity, Alarm::class.java)
                startActivity(intent)
            }
        }
    }

    private fun startSensor() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BODY_SENSORS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.BODY_SENSORS), 1001)
            return
        }
        if (heartRateSensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        gyroscope?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onResume() {
        super.onResume()
        // Registrar el listener del sensor
        heartRateSensor?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        // Detener el sensor cuando no esté en uso
        sensorManager.unregisterListener(this)
    }

    @SuppressLint("DefaultLocale")
    override fun onSensorChanged(event: SensorEvent?) {
        var bpm: Int = 0
        var ac: Int = 0
        var gyr: Int = 0
        if (event?.sensor?.type == Sensor.TYPE_HEART_RATE) {
            bpm = event.values[0].toInt()
            //Log.d("Pulso", "Frecuencia: $bpm")
            pulseTextView.text = bpm.toString()

            val color = when {
                bpm < 50 -> android.graphics.Color.rgb(255, 255, 255)
                bpm > 200 -> android.graphics.Color.RED
                bpm > 150 -> android.graphics.Color.rgb(255, 165, 0)
                bpm > 100 -> android.graphics.Color.YELLOW
                else -> android.graphics.Color.parseColor("#52A3FF")
            }

            pulseTextView.setTextColor(color)
        }

        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            val preAc = sqrt(((x * x + x) + (y * y + y) + (z * z)).toDouble())
            ac = preAc.toInt() - 9

            //Log.d("Acelerómetro", "X: $x, Y: $y, Z: $z, Total: $preAc")
            accelerometerTextView.text = ac.toString()
        }

        if (event?.sensor?.type == Sensor.TYPE_GYROSCOPE) {
            gyr = event.values[0].toInt()
            Log.d("Giroscopio", "Frecuencia: $gyr")
        }

        if (numTry == 0 && (bpm > 100 ) || (bpm < config.minBpm && bpm > 1)) {
            sendMessageToPhone("/alerta", "¡Pulso alto!", this)
            sendAlert(config.username, "ortizmedinajosefrancisco@gmail.com", "Alto ritmo caridaco") { success, message ->
                if (success) {
                    numTry ++
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Alerta enviada con exito", Toast.LENGTH_SHORT -10).show()
                    }
                } else {
                    numTry = 0
                    runOnUiThread {
                        Toast.makeText(
                            this@MainActivity,
                            "¡Ocurrio un error inesperado!",
                            Toast.LENGTH_SHORT - 10
                        ).show()
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No necesario para este caso
    }
}

@Composable
fun WearApp(greetingName: String) {
    SafeWatchTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            TimeText()
            Greeting(greetingName = greetingName)
        }
    }
}

@Composable
fun Greeting(greetingName: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = stringResource(R.string.hello_world, greetingName)
    )
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp("Preview Android")
}