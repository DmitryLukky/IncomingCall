package com.wexberry.incomingcall

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import com.wexberry.incomingcall.service.MyService
import kotlinx.android.synthetic.main.dialog_incoming_call.view.*
import kotlin.system.exitProcess

class OverlayActivity : AppCompatActivity() {

    lateinit var manager: WindowManager
    lateinit var params: WindowManager.LayoutParams
    lateinit var rootView: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overlay)

        // Даём активити возможность появляться на заблокированном экране
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)

            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        }

//        var windowTest: WindowManager.LayoutParams = window.attributes
//        windowTest.dimAmount = 0F
//        windowTest.flags = (WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
//
//        windowTest.width = WindowManager.LayoutParams.WRAP_CONTENT
//        windowTest.height = WindowManager.LayoutParams.WRAP_CONTENT
//
//        windowTest.format = PixelFormat.TRANSLUCENT
//
//        window.attributes = windowTest

        // Получаем номер телефона из ресивера
        val incoming_number: String = intent?.getStringExtra("incoming_number").toString()

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val intentMyService = Intent(this, MyService::class.java)
            intentMyService.putExtra("incoming_number", incoming_number)

            startForegroundService(intentMyService)
        }

        clearAllView()

        //val stopActivity: Boolean = intent.getBooleanExtra("stop", false)

//        if (stopActivity) {
//            clearAllView()
//        }

        // Показываем плавающее окно
        //service(incoming_number)
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
            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
            //WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
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

    fun clearAllView() {
        finishAffinity()
        exitProcess(0)
        //rootView.removeAllViews()
    }

    override fun onDestroy() {
        super.onDestroy()

        clearAllView()
    }
}