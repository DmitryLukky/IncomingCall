package com.wexberry.incomingcall.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.*
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.NotificationCompat
import com.wexberry.incomingcall.R
import kotlinx.android.synthetic.main.dialog_incoming_call.view.*


class MyService : Service() {

    lateinit var manager: WindowManager
    lateinit var params: WindowManager.LayoutParams
    lateinit var rootView: CardView

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        super.onCreate()
        showNotification()
    }

    // Вызывается при запуске сервиса
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Показываем уведомление о запущеном сервисе (Обязательно требуется с Андроид 8+, если этого не сделать, то сервис умрёт)
        //showNotification()

        // Получаем номер телефона из ресивера
        val incoming_number: String = intent?.getStringExtra("incoming_number").toString()

        // Запускаем сервис с вложенным в него номером телефона
        service(incoming_number)

        return super.onStartCommand(intent, flags, startId)
    }

    private fun showNotification() {
        if (Build.VERSION.SDK_INT >= 26) {
            val CHANNEL_ID = "Channel_01"
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Читаемое название канала",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            manager.createNotificationChannel(channel)
            val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Если не указать, то будут краши на Андроид 8
                .setContentTitle("Title")
                .setContentText("Text").build()
            startForeground(1, notification)
        }
    }

    @SuppressLint("ClickableViewAccessibility", "InflateParams")
    fun service(incoming_number: String) {
        manager = getSystemService(WINDOW_SERVICE) as WindowManager
        params = WindowManager.LayoutParams(
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR
            }, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )

        // Настраиваем ширину и высоту макета
        params.width = WindowManager.LayoutParams.WRAP_CONTENT
        params.height = WindowManager.LayoutParams.WRAP_CONTENT

        params.format = PixelFormat.TRANSLUCENT

        params.gravity = Gravity.TOP
        params.x = 235 // Координата по горизонтали
        params.y = 200 // Координата по вертикали

        rootView =
            LayoutInflater.from(applicationContext)
                .inflate(R.layout.dialog_incoming_call, null) as CardView

        manager.addView(rootView, params)

        // - - - Тут должна быть работа с БД - - -
        var name: String = when (incoming_number) {
            this.getString(R.string.number_dmitry) -> this.getString(R.string.name_dmitry)
            this.getString(R.string.number_ekaterina) -> this.getString(R.string.name_ekaterina)
            else -> "Неизвестный номер"
        }

        // Устанавливаем имя и номер звонящего
        rootView.name.text = name
        rootView.number.text = incoming_number

        Toast.makeText(this, "Тебе звонит: $name", Toast.LENGTH_LONG).show()

        // Перемещение окна
        rootView.findViewById<CardView>(R.id.dialog_incoming_call)
            .setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                    if (p1?.action == MotionEvent.ACTION_MOVE) {
                        // Обрабатываем позицию касания и обноваляем позицию/размер Layout'а
                        params.y = p1.getRawY().toInt()
                        params.x = p1.getRawX().toInt()
                        manager.updateViewLayout(rootView, params)
                    }
                    return true
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()

        rootView.removeAllViews()
    }
}
