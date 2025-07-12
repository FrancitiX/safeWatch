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
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import java.io.IOException
import java.time.LocalDateTime

private val client = OkHttpClient()

private val DB_URI = "https://csb2wwrf-3000.usw3.devtunnels.ms/"

fun sendAlert(
    user: String,
    email: String,
    description: String,
    onResult: (Boolean, String?) -> Unit // callback con resultado y mensaje
) {
    val json = JSONObject()
    json.put("user", user)
    json.put("email", email)
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
                    Log.d("SI JALA", data)
                    onResult(true, data) // éxito
                } catch (e: Exception) {
                    onResult(false, null) // error parseando
                    Log.e("ERROR AL CREAR REGISTRO!", response.toString() + e)
                }
            } else {
                Log.e("ERROR al realizar el registro", response.toString())
                onResult(false, null) // error en la respuesta
            }
        }
    })
}


fun getHistory(user: String, email: String, onResult: (Boolean, String?) -> Unit) {

    val url = "https://csb2wwrf-3000.usw3.devtunnels.ms/getRegistrations?user=$user&email=$email".toHttpUrl()

    val request = Request.Builder()
        .url(url)
        .get()
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("HTTP", "Error: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            val responseBody = response.body?.string()
            if (response.isSuccessful && responseBody != null) {
                try {
                    val jsonResponse = JSONObject(responseBody)
                    val data = jsonResponse.optString("data")
                    onResult(true, data)
                } catch (e: Exception) {
                    onResult(false, null)
                }
            } else {
                Log.e("Error al obtener el Historial", response.message)
                onResult(false, null)
            }
        }
    })
}



