package com.wexberry.incomingcall.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.*
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.wexberry.incomingcall.R
import kotlinx.android.synthetic.main.dialog_incoming_call.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MyService : Service() {

    lateinit var manager: WindowManager
    lateinit var rootView: ConstraintLayout

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val incoming_number: String = intent?.getStringExtra("incoming_number").toString()
        GlobalScope.launch(Dispatchers.Main){
        service(incoming_number)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    @SuppressLint("ClickableViewAccessibility", "InflateParams")
    fun service(incoming_number: String) {
        manager = getSystemService(WINDOW_SERVICE) as WindowManager
        val params = WindowManager.LayoutParams(
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        )

        // Настраиваем ширину и высоту макета
        params.width = WindowManager.LayoutParams.WRAP_CONTENT
        params.height = WindowManager.LayoutParams.WRAP_CONTENT

        PixelFormat.TRANSLUCENT
        PixelFormat.TRANSPARENT

        params.gravity = Gravity.TOP
        params.x = 300
        params.y = 200

        rootView =
            LayoutInflater.from(this)
                .inflate(R.layout.dialog_incoming_call, null) as ConstraintLayout

        manager.addView(rootView, params)

        // Перемещение окна
        rootView.findViewById<ConstraintLayout>(R.id.dialog_incoming_call)
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

        // - - - Тут должна быть работа с БД - - -
        var name: String = when (incoming_number) {
            this.getString(R.string.number_dmitry) -> this.getString(R.string.name_dmitry)
            this.getString(R.string.number_ekaterina) -> this.getString(R.string.name_ekaterina)
            else -> "Неизвестный номер"
        }

        Toast.makeText(this, "Тебе звонит: $name", Toast.LENGTH_LONG).show()

        // Устанавливаем имя и номер звонящего
        rootView.name.text = name
        rootView.number.text = incoming_number
    }

    override fun onDestroy() {
        super.onDestroy()

        rootView.removeAllViews()
        rootView.removeAllViews()
    }
}