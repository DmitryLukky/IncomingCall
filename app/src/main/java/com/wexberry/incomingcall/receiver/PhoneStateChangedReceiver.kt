package com.wexberry.incomingcall.receiver

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.telephony.TelephonyManager
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.wexberry.incomingcall.R
import kotlinx.android.synthetic.main.dialog_incoming_call.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


open class PhoneStateChangedReceiver : BroadcastReceiver() {

//    val CALL_INTERRUPTED: Int = 0 // Звонок прерван
//    val CALL: Int = 1 // Идёт вызов
//    val CALL_ACCEPTED: Int = 2 // Вызов принят (идёт разговор)

    var incomingNumber: String = "Неизвестный номер"

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(p0: Context?, p1: Intent?) {
        Log.d("DA", "Запустился onReceive")
        // Получаем состояние телефона
        val phone_state: String? = p1?.getStringExtra(TelephonyManager.EXTRA_STATE)
        Log.d("DA", "phone_state: $phone_state")

        if (phone_state == "RINGING") {
            Log.d("DA", "Звонок!")

            incomingNumber = p1.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER).toString() // Получаем входящий номер

            Log.d("DA", "Number: $incomingNumber")

            if (incomingNumber != "null") {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    CustomView(p0!!, incomingNumber).show()
                } else {
                    CustomView(p0!!, incomingNumber).show()
                }
            }
        } else if (phone_state == "IDLE") {
            Log.d("DA", "Конец звонка")
            CustomView(p0!!, incomingNumber).clearView()
        }
    }

    inner class CustomView(private val context: Context, private val incoming_number: String) {

        var manager: WindowManager = context.getSystemService(Service.WINDOW_SERVICE) as WindowManager
        var params: WindowManager.LayoutParams = WindowManager.LayoutParams(
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR
            }, //WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
        )
        var rootView: CardView = LayoutInflater.from(context).inflate(R.layout.dialog_incoming_call, null) as CardView

        // Создание окна
        fun createView() {
            // Настраиваем ширину и высоту макета
            params.width = WindowManager.LayoutParams.WRAP_CONTENT
            params.height = WindowManager.LayoutParams.WRAP_CONTENT

            // Система выбирает формат, поддерживающий полупрозрачность (много альфа-бит)
            params.format = PixelFormat.TRANSLUCENT

            // Настраиваем расположение окна
            params.gravity = Gravity.TOP
            params.x = 235 // Координата по горизонтали
            params.y = 200 // Координата по вертикали
        }

        // Перемещение окна
        @SuppressLint("ClickableViewAccessibility")
        fun movingTheWindow() {
            rootView.findViewById<CardView>(R.id.dialog_incoming_call).setOnTouchListener(object : View.OnTouchListener {
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

        // - - - Тут должна быть работа с БД - - -
        fun checkDatabase() {
            var name: String = when (incoming_number) {
                context.getString(R.string.number_dmitry) -> context.getString(R.string.name_dmitry)
                context.getString(R.string.number_ekaterina) -> context.getString(R.string.name_ekaterina)
                else -> "Неизвестный номер"
            }

            // Устанавливаем имя и номер звонящего
            rootView.name.text = name
            rootView.number.text = incoming_number

            Toast.makeText(context, "Тебе звонит: $name", Toast.LENGTH_LONG).show()
        }

        // Показываем окно
        fun show() {
            // Создаём окно
            createView()

            // Показываем окно
            manager.addView(rootView, params)

            // Проверяем базу данных
            checkDatabase()

            // Включаем перемещение окна
            movingTheWindow()
        }

        // Удаляем окно
        fun clearView() {
            Log.d("DA", "CustomView.clearView: ")
            rootView.removeAllViews()
        }
    }
}