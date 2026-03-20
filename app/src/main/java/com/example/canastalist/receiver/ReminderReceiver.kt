package com.example.canastalist.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.canastalist.MainActivity
import com.example.canastalist.R

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val itemName = intent.getStringExtra("ITEM_NAME") ?: "Artículo de tu lista"
        val itemId = intent.getStringExtra("ITEM_ID") ?: ""

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "shopping_reminders"

        // Crear canal para Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Recordatorios de Compra",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones para no olvidar comprar artículos"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Intent para abrir la app al tocar la notificación
        val activityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 
            itemId.hashCode(), 
            activityIntent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Construir la notificación con tu identidad visual
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(context.resources.getIdentifier("ic_app_logo", "drawable", context.packageName).let { 
                if (it != 0) it else android.R.drawable.ic_dialog_info 
            })
            .setContentTitle("¡No olvides comprar!")
            .setContentText(itemName)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(itemId.hashCode(), notification)
    }
}
