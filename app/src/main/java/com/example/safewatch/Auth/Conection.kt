package com.example.safewatch.Auth

import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import java.io.IOException
import java.time.LocalDateTime

private val client = OkHttpClient()

fun sendAlert() {
    val json = """
       {
          "tipo": "manual",
          "frecuencia": 140,
          "fecha": "${LocalDateTime.now()}"
       }
       """.trimIndent()

    val request = Request.Builder()
        .url("http://<IP-de-tu-servidor>:3000/alerta") // <- Reemplaza con tu IP real
        .post(RequestBody.create("application/json".toMediaType(), json))
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("HTTP", "Error: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            Log.d("HTTP", "Alerta enviada con éxito")
        }
    })
}

