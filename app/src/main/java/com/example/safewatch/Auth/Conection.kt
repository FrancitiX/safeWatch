package com.example.safewatch.Auth

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.example.safewatch.presentation.MainActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import java.io.IOException
import java.time.LocalDateTime

private val client = OkHttpClient()

private val DB_URI = "http://192.168.0.104:3000/"

fun sendAlert(
    user: String,
    description: String,
    onResult: (Boolean, String?) -> Unit // callback con resultado y mensaje
) {
    val json = JSONObject()
    json.put("user", user)
    json.put("description", description)

    val request = Request.Builder()
        .url(DB_URI + "createReg")
        .post(RequestBody.create("application/json".toMediaType(), json.toString()))
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("HTTP", "Error: ${e.message}")
            onResult(false, null) // falla
        }

        override fun onResponse(call: Call, response: Response) {
            val responseBody = response.body?.string()
            if (response.isSuccessful && responseBody != null) {
                try {
                    val jsonResponse = JSONObject(responseBody)
                    val data = jsonResponse.optString("data")
                    onResult(true, data) // éxito
                } catch (e: Exception) {
                    onResult(false, null) // error parseando
                }
            } else {
                onResult(false, null) // error en la respuesta
            }
        }
    })
}


fun getHistory() {
    val json = """
       {
          "user": "Francisco",
       }
       """.trimIndent()

    val request = Request.Builder()
        .url(DB_URI + "getRegistrations")
        .post(RequestBody.create("application/json".toMediaType(), json))
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("HTTP", "Error: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            val json = response.body?.string()
            if (json != null) {
                val jsonObject = JSONObject(json)
                Log.d("HTTP", "Dato recibido: $jsonObject")
            } else {
                Log.e("HTTP", "El cuerpo de la respuesta es nulo")
            }
        }
    })
}



