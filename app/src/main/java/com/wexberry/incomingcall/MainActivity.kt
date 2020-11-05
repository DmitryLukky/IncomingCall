package com.wexberry.incomingcall

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.wexberry.incomingcall.receiver.PhoneStateChangedReceiver
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initFunc()
    }

    private fun initFunc() {
        registerReceived() // Динамическая регистрация Ресивера
        permissionStatus() // Запрос разрешения (Андроид 6+)
        createNotificationChannel() // Создаём channel для notifications (Андроид 8+)
    }

    // Динамическая регистрация Ресивера
    private fun registerReceived() {
        val receiver = PhoneStateChangedReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
        registerReceiver(receiver, intentFilter)
    }

    // Запрашиваем разрешения с Андроид 6+ (М)
    private fun permissionStatus() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val permissionStatus: Int = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_PHONE_STATE
            )

            // Если разрешения не дано, то запрашиваем его
            if (permissionStatus == PackageManager.PERMISSION_DENIED) {
                // Список с разрешениями
                val permissions = arrayOf(android.Manifest.permission.READ_PHONE_STATE)
                // Запрашиваем разрешения из списка
                ActivityCompat.requestPermissions(this, permissions, 0)
            }
        }
    }

    // Проверка дал ли пользователь разрешения или нет
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // requestCode - тот, который мы указывали при запросе разрешений
        // permissions - список запрошенных разрешений
        // grantResults - список с ответами (разрешено или нет)
        if (requestCode == 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Спасибо", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Зря ты так сделал..", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Для версий Андроид Oreo (8) нужно создавать channel
    private fun createNotificationChannel() {
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