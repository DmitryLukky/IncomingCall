package com.wexberry.incomingcall

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wexberry.incomingcall.receiver.PhoneStateChangedReceiver


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        // Для версий Андроид Oreo (8) нужно создавать channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                PhoneStateChangedReceiver().CHANNEL_ID,
                "Channel notification",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Channel for notifications"
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(false)

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}