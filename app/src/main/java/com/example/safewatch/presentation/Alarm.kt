package com.example.safewatch.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.safewatch.Auth.InternalData
import com.example.safewatch.Auth.UserConfig
import com.example.safewatch.Auth.sendAlert
import com.example.safewatch.Auth.sendMessageToPhone
import com.example.safewatch.R
import kotlin.math.log

@Suppress("IMPLICIT_BOXING_IN_IDENTITY_EQUALS")
class Alarm : ComponentActivity() {

    val localStorage = InternalData(this)
    val username = localStorage.getData()?.username

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setContentView(R.layout.alarm)

        val recAlarm: CheckBox = findViewById(R.id.recAlert)

        val confirm: ImageButton = findViewById(R.id.confirm)
        val cancel: ImageButton = findViewById(R.id.cancel)

        recAlarm.setOnCheckedChangeListener { _, isChecked ->
            val config = localStorage.getData()
            if (config != null) {

                config.recAlert = isChecked
                localStorage.save(config)

                Log.d("configuacion", localStorage.getData().toString())
            } else {
                Log.e("CheckBox", "No se pudo cargar la configuración")
            }
        }

        cancel.setOnClickListener{
            val intent = Intent(this@Alarm, MainActivity::class.java)
            startActivity(intent)
        }

        confirm.setOnClickListener {
            sendMessageToPhone("/alerta", "¡Pulso alto!", this)

            sendAlert("Francisco", "ortizmedinajosefrancisco@gmail.com", "Alerta manual") { success, message ->
                if (success) {
                    runOnUiThread {
                        Toast.makeText(
                            this@Alarm,
                            "Alerta enviada con exito",
                            Toast.LENGTH_SHORT - 10
                        ).show()
                    }
                    Handler(Looper.getMainLooper()).postDelayed({
                        val intent = Intent(this@Alarm, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }, 1000)
                } else {
                    runOnUiThread {
                        Toast.makeText(
                            this@Alarm,
                            "¡Ocurrio un error inesperado!",
                            Toast.LENGTH_SHORT - 10
                        ).show()
                    }
                }
            }

        }
    }

}
