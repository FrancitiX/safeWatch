package com.example.safewatch

import Auth.InternalData
import Auth.UserConfig
import Auth.addConfig
import Auth.getConfig
import Auth.getHistory
import Auth.login
import Auth.singin
import android.annotation.SuppressLint
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
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginTop
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.safewatch.ui.theme.SafeWatchTheme
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.log

class MainActivity : ComponentActivity() {
    private lateinit var config: UserConfig
    private lateinit var localStorage: InternalData
    private val interval = 30_000L

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        localStorage = InternalData(this)
        config = localStorage.getData() ?: UserConfig("", "", 200, 35, 100, false, false)

        Log.d("Inicio", config.toString())
        if (config.isLoggedIn == true) {
            setContentView(R.layout.main)

            val profile: ImageButton = findViewById(R.id.profile)
            val loadingGif: ImageView = findViewById(R.id.Carga)
            val recyclerView = findViewById<RecyclerView>(R.id.registroRecycler)
            //val noHistory: TextView = findViewById(R.id.noHistory)

            recyclerView.visibility = View.GONE
            loadingGif.visibility = View.VISIBLE
            Glide.with(this).asGif().load(R.drawable.carga).into(loadingGif)

            getHistory(config.username, config.email) { success, message ->
                runOnUiThread {
                    loadingGif.visibility = View.GONE

                    if (success && message != null) {
                        try {
                            val jsonArray = JSONArray(message)
                            val registroList = mutableListOf<Registro>()

                            for (i in 0 until jsonArray.length()) {
                                val item = jsonArray.getJSONObject(i)
                                val user = item.getString("user")
                                val description = item.getString("description")
                                val dateObj = item.getJSONObject("date")
                                val date = dateObj.getString("date")
                                val time = dateObj.getString("time")

                                registroList.add(Registro(user, description, date, time))
                            }

                            if (registroList.isNotEmpty()) {
                                recyclerView.visibility = View.VISIBLE
                                recyclerView.layoutManager = LinearLayoutManager(this)
                                recyclerView.adapter = RegistroAdapter(registroList)
                            } else {
                                //noHistory.visibility = View.VISIBLE
                            }

                        } catch (e: Exception) {
                            Log.e("Error procesando historial", e.toString())
                            //noHistory.visibility = View.VISIBLE
                        }
                    } else {
                        //noHistory.visibility = View.VISIBLE
                    }
                }
            }

            profile.setOnClickListener {
                val intent = Intent(this, Profile::class.java)
                startActivity(intent)
            }

        } else {
            setContentView(R.layout.log_in)

            val log_in: Button = findViewById(R.id.logIn)
            var userInput: EditText = findViewById(R.id.user)
            var emailInput: EditText = findViewById(R.id.email)

            log_in.setOnClickListener {
                val loadingGif: ImageView = findViewById(R.id.Carga)
                val user = userInput.text.toString().trim()
                val email = emailInput.text.toString().trim()
                val infoExtra: TextView = findViewById(R.id.extraInfo)
                val params = infoExtra.layoutParams as ConstraintLayout.LayoutParams

                if ((user == "") || email == "") {
                    Toast.makeText(
                        this@MainActivity,
                        "Por favor ingrese los datos requeridos",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    params.topMargin = (38 * resources.displayMetrics.density).toInt()
                    infoExtra.layoutParams = params
                    log_in.visibility = View.GONE
                    loadingGif.visibility = View.VISIBLE
                    Glide.with(this).asGif().load(R.drawable.carga).into(loadingGif)
                    login(user, email) { success, message ->
                        if (success) {
                            getConfig(user, email) {success, message ->
                                if (success) {
                                    //Log.d("SUceess", message.toString())
                                    val json = JSONObject(message.toString())
                                    runOnUiThread {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Incio de sesión correcto",
                                            Toast.LENGTH_SHORT - 10
                                        ).show()
                                    }
                                    val newConfig = UserConfig(
                                        username = user,
                                        email = email,
                                        maxBpm = json.getInt("max_HEART"),
                                        minBpm = json.getInt("min_HEART"),
                                        minAcceleration = json.getInt("min_AC"),
                                        recAlert  = json.getBoolean("confirmAlert"),
                                        isLoggedIn = true
                                    )
                                    Log.d("NUEVA CONFIGURACION", newConfig.toString())
                                    localStorage.save(newConfig)

                                    // Reinier la actividad
                                    finish()
                                    startActivity(Intent(this, MainActivity::class.java))
                                } else {
                                    val originalMargin = (0 * resources.displayMetrics.density).toInt()
                                    params.topMargin = originalMargin
                                    infoExtra.layoutParams = params
                                    log_in.visibility = View.VISIBLE
                                    loadingGif.visibility = View.GONE
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Ocurrio un error inesperado",
                                        Toast.LENGTH_SHORT - 10
                                    ).show()
                                }
                            }
                        } else {
                            //loadingGif.visibility = View.GONE
                            runOnUiThread {
                                if (message.toString() == "Usuario no registrado") {
                                    sing_in(this) { confirm ->
                                        if (confirm) {
                                            Handler(Looper.getMainLooper()).postDelayed({
                                                Toast.makeText(
                                                    this@MainActivity,
                                                    "Registrando usuario...",
                                                    Toast.LENGTH_SHORT - 10
                                                ).show()

                                                singin(user, email) { success, message ->
                                                    if (success) {
                                                        addConfig(user, email) {success, message ->
                                                            if (!success) {
                                                                Log.e("Error en la configuración", message.toString())
                                                            }
                                                        }
                                                        Handler(Looper.getMainLooper()).postDelayed(
                                                            {
                                                                Toast.makeText(
                                                                    this@MainActivity,
                                                                    "¡Registro exitoso! iniciando sesión",
                                                                    Toast.LENGTH_SHORT - 10
                                                                ).show()
                                                                login(user, email) { success, message ->
                                                                    if (success) {
                                                                        getConfig(user, email) { success, message ->
                                                                            val newConfig = UserConfig(
                                                                                username = user,
                                                                                email = email,
                                                                                maxBpm = 220,
                                                                                minBpm = 35,
                                                                                100,
                                                                                recAlert  = false,
                                                                                isLoggedIn = true
                                                                            )
                                                                            localStorage.save(newConfig)

                                                                            // Reiniciar la actividad
                                                                            finish()
                                                                            startActivity(Intent(this, MainActivity::class.java))
                                                                        }

                                                                    } else {
                                                                        val originalMargin = (0 * resources.displayMetrics.density).toInt()
                                                                        params.topMargin = originalMargin
                                                                        infoExtra.layoutParams = params
                                                                        log_in.visibility = View.VISIBLE
                                                                        loadingGif.visibility = View.GONE
                                                                        Toast.makeText(
                                                                            this@MainActivity,
                                                                            "Ocurrio un error inesperado",
                                                                            Toast.LENGTH_SHORT - 10
                                                                        ).show()
                                                                    }
                                                                }
                                                            }, 500)
                                                    } else {

                                                    }
                                                }
                                            }, 500)
                                        } else {
                                            val originalMargin = (0 * resources.displayMetrics.density).toInt()
                                            params.topMargin = originalMargin
                                            infoExtra.layoutParams = params
                                            log_in.visibility = View.VISIBLE
                                            loadingGif.visibility = View.GONE
                                        }
                                    }
                                } else {
                                    val originalMargin = (0 * resources.displayMetrics.density).toInt()
                                    params.topMargin = originalMargin
                                    infoExtra.layoutParams = params
                                    log_in.visibility = View.VISIBLE
                                    loadingGif.visibility = View.GONE
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
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SafeWatchTheme {
        Greeting("Android")
    }
}