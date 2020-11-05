package com.wexberry.incomingcall.receiver

import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.wexberry.incomingcall.MainActivity
import com.wexberry.incomingcall.R

open class PhoneStateChangedReceiver : BroadcastReceiver() {

    val CHANNEL_ID: String = "Channel notification"

    override fun onReceive(p0: Context?, p1: Intent?) {
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

                // - - - Тут должна быть работа с БД - - -
                var name: String = when (incoming_number) {
                    p0?.getString(R.string.number_dmitry) -> p0.getString(R.string.name_dmitry)
                    p0?.getString(R.string.number_ekaterina) -> p0.getString(R.string.name_ekaterina)
                    else -> "Неизвестный номер"
                }

                Toast.makeText(p0, "Тебе звонит: $name", Toast.LENGTH_LONG).show()

                // - - - Отправка пуша - - -
                // При нажатии на уведомление, будем запускать MainActivity
                val notificationIntent = Intent(p0, MainActivity::class.java)
                val contentIntent = PendingIntent.getActivity(
                    p0,
                    0, notificationIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT
                )

                // Создаём уведомление и настраиваем его
                val builder: NotificationCompat.Builder =
                    NotificationCompat.Builder(p0!!, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher) // Иконка уведомления
                        .setColor(p0.resources.getColor(R.color.black)) // Цвет иконки и названия приложения в уведомлении
                        .setContentTitle(name) // Заголовок уведомления
                        .setContentText(incoming_number) // Текст в уведомлении
                        .setPriority(NotificationCompat.PRIORITY_MAX) // Приоритет HIGH позволяет показывать всплывающее уведомление на экране (выпрыгивает сверху и закрывается через какое-то время)
                        .setContentIntent(contentIntent)
                        .setAutoCancel(true) // Автоматически закрыть уведомление после нажатия
                        .setDefaults(Notification.DEFAULT_ALL) // Звук, вибрация и индикация светодидоами выставляется по умолчанию

                // Создаем NotificationManager
                val notificationManager: NotificationManagerCompat =
                    NotificationManagerCompat.from(p0)

                // Показываем уведомление. Указываем id уведомления, чтобы показывать несколько уведомлений,
                // нужно каждому дать уникальные id. Если вызвать несколько уведомлений с одним id, то оно просто обновится.
                notificationManager.notify(101, builder.build())
            }
        }
    }
}