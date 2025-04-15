package com.example.bloodpressureapp.receiver

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.bloodpressureapp.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val message = intent.getStringExtra("message") ?: "⏰ Erinnerung"

        // Ab Android 13: Benachrichtigungsrecht prüfen
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            Toast.makeText(context, "❌ Keine Berechtigung für Benachrichtigungen", Toast.LENGTH_SHORT).show()
            return
        }

        // 🔔 Notification Channel für Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "reminder_channel",
                "Erinnerungen",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Erinnerungen für Messungen oder Therapien"
            }

            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, "reminder_channel")
            .setSmallIcon(R.drawable.ic_notifications) // Passe ggf. dein Icon an
            .setContentTitle("Erinnerung")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(context)

        // ✅ Jetzt sicher: Benachrichtigung anzeigen
        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}
