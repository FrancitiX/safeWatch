package com.example.safewatch.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.safewatch.Auth.sendAlert
import com.example.safewatch.R

@Suppress("IMPLICIT_BOXING_IN_IDENTITY_EQUALS")
class Alarm : ComponentActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContentView(R.layout.alarm)

        val confirm: ImageButton = findViewById(R.id.confirm)
        val cancel: ImageButton = findViewById(R.id.cancel)


        cancel.setOnClickListener{
            val intent = Intent(this@Alarm, MainActivity::class.java)
            startActivity(intent)
        }

        confirm.setOnClickListener {
            sendAlert("Francisco", "Alerta manual") { success, message ->
                if (success) {
                    Toast.makeText(this@Alarm, "Alerta enviada con exito", Toast.LENGTH_SHORT -10).show()

                    Handler(Looper.getMainLooper()).postDelayed({
                        val intent = Intent(this@Alarm, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }, 1000)
                } else {
                    Toast.makeText(this@Alarm, "¡Ocurrio un error inesperado!", Toast.LENGTH_SHORT -10).show()
                }
            }
        }
    }

}
