package com.wexberry.incomingcall.receiver

import android.annotation.SuppressLint
import android.app.Activity
import android.app.KeyguardManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.KEYGUARD_SERVICE
import android.content.Intent
import android.os.PowerManager
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat.getSystemService
import com.wexberry.incomingcall.service.MyService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


open class PhoneStateChangedReceiver : BroadcastReceiver() {

    @SuppressLint("ClickableViewAccessibility")
    override fun onReceive(p0: Context?, p1: Intent?) {
        // С Андроида 6+ ресивер вызывается 2 раза.
        // Один с номером телефона, второй без, поэтому ловим один раз с номером телефона:
        if (p1 != null) {
            if (p1.action.equals("android.intent.action.PHONE_STATE")) {
                // Получаем состояние телефона
                val phone_state: String? = p1.getStringExtra(TelephonyManager.EXTRA_STATE)

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

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        p0?.startForegroundService(intentMyService)
                    } else {
                        p0?.startService(intentMyService)
                    }
                } else if (phone_state == "IDLE") {
                    // Останавливаем сервис отображения окна
                    val intentMyService = Intent(p0, MyService::class.java)
                    p0?.stopService(intentMyService)
                }

            }
        }
    }
}