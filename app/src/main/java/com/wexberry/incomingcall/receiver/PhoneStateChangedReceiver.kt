package com.wexberry.incomingcall.receiver

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.ColorDrawable
import android.telephony.TelephonyManager
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import com.wexberry.incomingcall.MainActivity
import com.wexberry.incomingcall.R
import com.wexberry.incomingcall.service.MyService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

open class PhoneStateChangedReceiver : BroadcastReceiver() {

    val CHANNEL_ID: String = "Channel notification"

    @SuppressLint("ClickableViewAccessibility")
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p1 != null) {
            if (p1?.action.equals("android.intent.action.PHONE_STATE")) {
                // Получаем состояние телефона
                val phone_state: String? = p1?.getStringExtra(TelephonyManager.EXTRA_STATE)

                // Если состояние телефона - звонок, то выполняем код
                // if (phone_state == p1?.getStringExtra(TelephonyManager.EXTRA_STATE_RINGING)) - гугл говорит,
                // что должен быть этот код, но мне присылает null, а должен "RINGING". Поэтому я проверяю вручную:
                if (phone_state == "RINGING") {
                    val incoming_number: String =
                        p1.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                            .toString() // Получаем входящий номер

                    // Запускаем сервис отображения окна
                    val intentMyService = Intent(p0, MyService::class.java)
                    intentMyService.putExtra("incoming_number", incoming_number)
                    p0?.startService(intentMyService)
                } else if (phone_state == "IDLE") {
                    // Останавливаем сервис отображения окна
                    val intentMyService = Intent(p0, MyService::class.java)
                    p0?.stopService(intentMyService)
                }
            }
        }
    }
}

//val dialog: Dialog = Dialog(p0!!) // Создание диалогового окна
//dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // Убираем заголовок диалогового окна
//dialog.setContentView(R.layout.dialog_incoming_call) // Вставляем наш макет
//dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // Делаем фон за диалоговым окном прозрачным
//dialog.setCancelable(false) // Убираем возможность закрыть диалоговое окно с помощью системной кнопки "Назад"
//dialog.window?.setLayout(
//ViewGroup.LayoutParams.MATCH_PARENT,
//ViewGroup.LayoutParams.MATCH_PARENT
//) // ВАЖНО! Без этой строчки кода, диалоговое окно будет узким.
//dialog.show() // Показываем диалоговое окно
//
//
//// - - - Отправка пуша - - -
//// При нажатии на уведомление, будем запускать MainActivity
//val notificationIntent = Intent(p0, MainActivity::class.java)
//val contentIntent = PendingIntent.getActivity(
//    p0,
//    0, notificationIntent,
//    PendingIntent.FLAG_CANCEL_CURRENT
//)
//
//// Создаём уведомление и настраиваем его
//val builder: NotificationCompat.Builder =
//    NotificationCompat.Builder(p0!!, CHANNEL_ID)
//        .setSmallIcon(R.mipmap.ic_launcher) // Иконка уведомления
//        .setColor(p0.resources.getColor(R.color.black)) // Цвет иконки и названия приложения в уведомлении
//        .setContentTitle(name) // Заголовок уведомления
//        .setContentText(incoming_number) // Текст в уведомлении
//        .setPriority(NotificationCompat.PRIORITY_MAX) // Приоритет HIGH позволяет показывать всплывающее уведомление на экране (выпрыгивает сверху и закрывается через какое-то время)
//        .setContentIntent(contentIntent)
//        .setAutoCancel(true) // Автоматически закрыть уведомление после нажатия
//        .setDefaults(Notification.DEFAULT_ALL) // Звук, вибрация и индикация светодидоами выставляется по умолчанию
//
//// Создаем NotificationManager
//val notificationManager: NotificationManagerCompat =
//    NotificationManagerCompat.from(p0)
//
//// Показываем уведомление. Указываем id уведомления, чтобы показывать несколько уведомлений,
//// нужно каждому дать уникальные id. Если вызвать несколько уведомлений с одним id, то оно просто обновится.
//notificationManager.notify(101, builder.build())