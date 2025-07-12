package Auth

import android.content.Context
import com.google.gson.Gson

data class UserConfig(
    val username: String,
    val email: String,
    val maxBpm: Int = 200,
    val minBpm: Int = 35,
    val minAcceleration: Int = 100,
    var recAlert: Boolean = false,
    var isLoggedIn: Boolean = false
)

class InternalData(private val context: Context) {
    private val gson = Gson()
    private val fileName = "config.json"

    fun save(config: UserConfig) {
        val jsonString = gson.toJson(config)
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(jsonString.toByteArray())
        }
    }

    fun getData(): UserConfig? {
        return try {
            val json = context.openFileInput(fileName).bufferedReader().use { it.readText() }
            gson.fromJson(json, UserConfig::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
