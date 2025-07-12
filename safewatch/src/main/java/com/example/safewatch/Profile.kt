package com.example.safewatch

import Auth.InternalData
import Auth.UserConfig
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge

class Profile: ComponentActivity() {
    private lateinit var config: UserConfig
    private lateinit var localStorage: InternalData

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        localStorage = InternalData(this)
        config = localStorage.getData() ?: UserConfig(
            username = "",
            email = "",
            maxBpm = 200,
            minBpm = 35,
            minAcceleration = 100,
            recAlert = false,
            isLoggedIn = false
        )

        setContentView(R.layout.profile)

        val user: TextView = findViewById(R.id.username)
        val email: TextView = findViewById(R.id.email)

        user.text = config.username
        email.text = config.email

        val minHeart : EditText = findViewById(R.id.minHeartRate)
        val maxHeart : EditText = findViewById(R.id.maxHeartRate)
        val minAccelerate : EditText = findViewById(R.id.minAccelerateRate)
        val confirmAlert: CheckBox = findViewById(R.id.checkBoxAlert)

        minHeart.setText(config.minBpm.toString())
        maxHeart.setText(config.maxBpm.toString())
        minAccelerate.setText(config.minAcceleration.toString())
        confirmAlert.isChecked = config.recAlert


        val logout : ImageButton = findViewById(R.id.log_out)

        logout.setOnClickListener {
            val config = localStorage.getData()
            if (config != null) {
                val newConfig = UserConfig(
                    username = "",
                    email = "",
                    maxBpm = 200,
                    minBpm = 35,
                    recAlert  = false,
                    isLoggedIn = false
                )
                localStorage.save(newConfig)
                finish()
                startActivity(Intent(this, MainActivity::class.java))
            }
        }

    }
}