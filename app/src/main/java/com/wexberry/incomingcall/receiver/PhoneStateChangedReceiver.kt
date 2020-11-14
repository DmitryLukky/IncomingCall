package com.wexberry.incomingcall.receiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telecom.Call
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import com.wexberry.incomingcall.service.MyService


open class PhoneStateChangedReceiver : BroadcastReceiver() {

    val CALL_INTERRUPTED: Int = 0 // Звонок прерван
    val CALL: Int = 1 // Идёт вызов
    val CALL_ACCEPTED: Int = 2 // Вызов принят (идёт разговор)

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(p0: Context?, p1: Intent?) {
        // С Андроида 6+ ресивер вызывается 2 раза, один с номером телефона, второй без.
        val telephony: TelephonyManager =
            p0?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        // Слушаем состояние вызова и получаем номер телефона
        telephony.listen(object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                super.onCallStateChanged(state, phoneNumber)

                val incoming_number: String = phoneNumber.toString()

                if (state == CALL) {
                    // Запускаем сервис отображения окна
                    val intentMyService = Intent(p0, MyService::class.java)
                    intentMyService.putExtra("incoming_number", incoming_number)

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        p0.startForegroundService(intentMyService)
                    } else {
                        p0.startService(intentMyService)
                    }
                } else if (state == CALL_INTERRUPTED) {
                    // Останавливаем сервис отображения окна
                    val intentMyService = Intent(p0, MyService::class.java)
                    p0.stopService(intentMyService)
                }
            }
        }, PhoneStateListener.LISTEN_CALL_STATE)
    }
}


// - - - Другой способ - - -
//if (p1?.action.equals("android.intent.action.PHONE_STATE")) {
//    // Получаем состояние телефона
//    val phone_state: String? = p1?.getStringExtra(TelephonyManager.EXTRA_STATE)
//
//    // Если состояние телефона - звонок, то выполняем код
//    // if (phone_state == p1?.getStringExtra(TelephonyManager.EXTRA_STATE_RINGING)) - гугл говорит,
//    // что должен быть этот код, но мне присылает null, а должен "RINGING". Поэтому я проверяю вручную:
//    if (phone_state == "RINGING") {
//        var incoming_number: String =
//            p1.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
//                .toString() // Получаем входящий номер
//        Log.d("TAG", "Number: $incoming_number")
//
//        // Запускаем сервис отображения окна
//        val intentMyService = Intent(p0, MyService::class.java)
//        intentMyService.putExtra("incoming_number", incoming_number)
//
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            p0?.startForegroundService(intentMyService)
//        } else {
//            p0?.startService(intentMyService)
//        }
//    } else if (phone_state == "IDLE") {
//        // Останавливаем сервис отображения окна
//        val intentMyService = Intent(p0, MyService::class.java)
//        p0?.stopService(intentMyService)
//    }
//}


//        val CALL_INTERRUPTED: Int = 0 // Звонок прерван
//        val CALL: Int = 1 // Идёт вызов
//        val CALL_ACCEPTED: Int = 2 // Вызов приянят (идёт разговор)
//
//        // С Андроида 6+ ресивер вызывается 2 раза, один с номером телефона, второй без.
//        val telephony: TelephonyManager =
//            p0?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//
//        // Слушаем состояние вызова и получаем номер телефона
//        telephony.listen(object : PhoneStateListener() {
//            override fun onCallStateChanged(state: Int, phoneNumber: String?) {
//                super.onCallStateChanged(state, phoneNumber)
//                Log.d("TAG", "number: $phoneNumber")
//
//                val incoming_number: String = phoneNumber.toString()
//
//                if (state == CALL) {
//                    // Запускаем сервис отображения окна
//                    val intentMyService = Intent(p0, MyService::class.java)
//                    intentMyService.putExtra("incoming_number", incoming_number)
//
//                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                        p0.startForegroundService(intentMyService)
//                    } else {
//                        p0.startService(intentMyService)
//                    }
//                } else if (state == CALL_INTERRUPTED) {
//                    // Останавливаем сервис отображения окна
//                    val intentMyService = Intent(p0, MyService::class.java)
//                    p0.stopService(intentMyService)
//                }
//            }
//        }, PhoneStateListener.LISTEN_CALL_STATE)