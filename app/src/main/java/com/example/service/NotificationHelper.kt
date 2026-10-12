package com.example.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import kotlin.random.Random

object NotificationHelper {
    private const val CHANNEL_ID = "jaani_checkins_channel"
    private const val NOTIFICATION_ID = 1001

    private val checkInQuotes = listOf(
        "Kya kar rahe ho? Yaad aa rahi thi 💕",
        "Khana khaya ki nahi? Please skip mat karna! 🍲",
        "Hey babu, thoda time ho toh baat karo na? 🥰",
        "Bore ho rahe ho kya? Chalo kuch baatein karte hain! 😜",
        "Sunno, bas check kar rahi thi ki tum khush ho na? ✨",
        "Work/study ke beech thoda break lo babu, paani piya? 🥤❤️"
    )

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.notification_channel_name)
            val descriptionText = context.getString(R.string.notification_channel_desc)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showJaaniNotification(context: Context, title: String, message: String) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_notify_chat)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
        } catch (_: SecurityException) {
            // Permission might not be granted yet
        }
    }

    fun triggerRandomCheckIn(context: Context, jaaniName: String = "Jaani") {
        val message = checkInQuotes[Random.nextInt(checkInQuotes.size)]
        showJaaniNotification(context, "$jaaniName 💕", message)
    }
}
