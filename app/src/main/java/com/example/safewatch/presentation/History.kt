package com.example.safewatch.presentation

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.safewatch.Auth.getHistory
import com.example.safewatch.R
import org.json.JSONArray

class History: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.history)

        getHistory("Francisco") { success, message ->
            if (success) {
                //Log.d("Resultado", message.toString())
                val jsonArray = JSONArray(message)

                runOnUiThread {
                    val container = findViewById<LinearLayout>(R.id.registroContainer)

                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getJSONObject(i)

                        val description = item.getString("description")
                        val dateObj = item.getJSONObject("date")
                        val date = dateObj.getString("date")
                        val time = dateObj.getString("time")

                        val textView = TextView(this)
                        val fullText = "$description\n📅 $date  |  🕒 $time"
                        val spannable = SpannableString(fullText)

                        val start = description.length + 1
                        val end = fullText.length

                        spannable.setSpan(
                            RelativeSizeSpan(0.8f),
                            start,
                            end,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )

                        textView.text = spannable
                        textView.setPadding(2, 5, 2, 5)
                        textView.textSize = 13f
                        textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
                        textView.gravity = Gravity.CENTER


                        container.addView(textView)
                    }
                }
            } else {
                //Toast.makeText(this@History, "¡Ocurrio un error inesperado!", Toast.LENGTH_SHORT -10).show()
                Log.e("Error en Historial", message.toString())
            }
        }
    }

}