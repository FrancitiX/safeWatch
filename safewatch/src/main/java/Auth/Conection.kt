package Auth

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import java.io.IOException
import java.time.LocalDateTime

private val client = OkHttpClient()

//private val DB_URI = "http://192.168.0.104:3000/"
private val DB_URI = "https://csb2wwrf-3000.usw3.devtunnels.ms/"


fun singin(
    user: String,
    email: String,
    onResult: (Boolean, String?) -> Unit // callback con resultado y mensaje
) {
    val json = JSONObject()
    json.put("user", user)
    json.put("email", email)

    val request = Request.Builder()
        .url(DB_URI + "newUser")
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
                    //Log.d("Resultado", data.toString())

                    onResult(true, data)
                } catch (e: Exception) {
                    //Log.e("error", response.toString())
                    onResult(false, null)
                }
            } else {
                val jsonResponse = JSONObject(responseBody)
                val data = jsonResponse.optString("data")
                //Log.e("Resultado error", data.toString())

                onResult(false, data) // error en la respuesta
            }
        }
    })
}

fun login(
    user: String,
    email: String,
    onResult: (Boolean, String?) -> Unit // callback con resultado y mensaje
) {
    val json = JSONObject()
    json.put("user", user)
    json.put("email", email)

    val request = Request.Builder()
        .url(DB_URI + "login")
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
                    //Log.d("Resultado", data.toString())

                    onResult(true, data)
                } catch (e: Exception) {
                    //Log.e("error", response.toString())
                    onResult(false, null)
                }
            } else {
                val jsonResponse = JSONObject(responseBody)
                val data = jsonResponse.optString("data")
                //Log.e("Resultado error", data.toString())

                onResult(false, data) // error en la respuesta
            }
        }
    })
}


fun getHistory(user: String, email: String, onResult: (Boolean, String?) -> Unit) {
    val json = JSONObject()
    json.put("user", user)

    val url = "https://csb2wwrf-3000.usw3.devtunnels.ms/getRegistrations?user=$user&email=$email".toHttpUrl()
    /*
    val url = HttpUrl.Builder()
        .scheme("http")
        .host("192.168.0.104")
        .port(3000)
        .addPathSegment("getRegistrations")
        .addQueryParameter("user", "Francisco")
        .build()
     */

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
                    val data = jsonResponse.getJSONArray("data")
                    Log.e("SI", data.toString())
                    onResult(true, data.toString())
                } catch (e: Exception) {
                    Log.e("ERROR EN HISTORIAL", responseBody)
                    onResult(false, null)
                }
            } else {
                Log.e("Error al obtener el Historial", response.message)
                onResult(false, null)
            }
        }
    })
}

fun addConfig(
    user: String,
    email: String,
    onResult: (Boolean, String?) -> Unit // callback con resultado y mensaje
) {
    val json = JSONObject()
    json.put("user", user)
    json.put("email", email)

    val request = Request.Builder()
        .url(DB_URI + "createConfig")
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
                    //Log.d("Resultado", data.toString())

                    onResult(true, data)
                } catch (e: Exception) {
                    //Log.e("error", response.toString())
                    onResult(false, null)
                }
            } else {
                val jsonResponse = JSONObject(responseBody)
                val data = jsonResponse.optString("data")
                //Log.e("Resultado error", data.toString())

                onResult(false, data) // error en la respuesta
            }
        }
    })
}

fun getConfig(user: String, email: String, onResult: (Boolean, String?) -> Unit) {

    val url = "https://csb2wwrf-3000.usw3.devtunnels.ms/getConfig?user=$user&email=$email".toHttpUrl()
    /*
    val url = HttpUrl.Builder()
        .scheme("http")
        .host("192.168.0.104")
        .port(3000)
        .addPathSegment("getRegistrations")
        .addQueryParameter("user", "Francisco")
        .build()
     */

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

fun updateConfig(
    user: String,
    email: String,
    min_HEART: Int,
    max_HEART: Int,
    min_AC: Int,
    confirmAlert: Boolean,
    onResult: (Boolean, String?) -> Unit // callback con resultado y mensaje
) {
    val json = JSONObject()
    json.put("user", user)
    json.put("email", email)
    json.put("min_HEART", min_HEART)
    json.put("max_HEART", max_HEART)
    json.put("min_AC", min_AC)
    json.put("confirmAlert", confirmAlert)

    val request = Request.Builder()
        .url(DB_URI + "newUser")
        .put(RequestBody.create("application/json".toMediaType(), json.toString()))
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
                    //Log.d("Resultado", data.toString())

                    onResult(true, data)
                } catch (e: Exception) {
                    //Log.e("error", response.toString())
                    onResult(false, null)
                }
            } else {
                val jsonResponse = JSONObject(responseBody)
                val data = jsonResponse.optString("data")
                //Log.e("Resultado error", data.toString())

                onResult(false, data) // error en la respuesta
            }
        }
    })
}

