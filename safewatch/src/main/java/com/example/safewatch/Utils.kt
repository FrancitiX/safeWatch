package com.example.safewatch

import android.app.AlertDialog
import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Registro(
    val user: String,
    val description: String,
    val date: String,
    val time: String
)


fun sing_in(context: Context, callback: (Boolean) -> Unit) {
    AlertDialog.Builder(context)
        .setTitle("Usuario no encontrado")
        .setMessage("¿Desea registrar el usuario?")
        .setPositiveButton("Sí") { dialog, _ ->
            callback(true)
            dialog.dismiss()
        }
        .setNegativeButton("No") { dialog, _ ->
            callback(false)
            dialog.dismiss()
        }
        .show()
}

class RegistroAdapter(private val registros: List<Registro>) :
    RecyclerView.Adapter<RegistroAdapter.RegistroViewHolder>() {

    class RegistroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.name)
        val descriptionText: TextView = itemView.findViewById(R.id.description)
        val dateText: TextView = itemView.findViewById(R.id.date)
        val timeText: TextView = itemView.findViewById(R.id.time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegistroViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.registration_item, parent, false)
        return RegistroViewHolder(view)
    }

    override fun onBindViewHolder(holder: RegistroViewHolder, position: Int) {
        val registro = registros[position]
        holder.nameText.text = registro.user
        holder.descriptionText.text = registro.description
        holder.dateText.text = registro.date
        holder.timeText.text = registro.time
    }

    override fun getItemCount(): Int = registros.size
}

data class ConfigResponse(
    val user: String,
    val email: String,
    val min_HEART: Int,
    val max_HEART: Int,
    val min_AC: Int,
    val min_SPO2: Int,
    val max_SPO2: Int,
    val confirmAlert: Boolean
)

data class RegistroAlerts(
    val user: String,
    val description: String,
    val date: String,
    val time: String
)
