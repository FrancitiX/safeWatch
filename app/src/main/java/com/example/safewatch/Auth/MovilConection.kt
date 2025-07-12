package com.example.safewatch.Auth

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.Wearable


fun sendMessageToPhone(path: String, message: String, context: Context) {
    //Log.d("Telefono w", "Se envio info al telefono")
    val nodeClient = Wearable.getNodeClient(context)
    val messageClient = Wearable.getMessageClient(context)

    nodeClient.connectedNodes.addOnSuccessListener { nodes ->
        for (node in nodes) {
            messageClient.sendMessage(node.id, path, message.toByteArray())
                .addOnSuccessListener {
                    Log.d("Wear", "Mensaje enviado al teléfono con éxito")
                }
                .addOnFailureListener {
                    Log.e("Wear", "Error al enviar mensaje al teléfono", it)
                }
            }
        }
    }
