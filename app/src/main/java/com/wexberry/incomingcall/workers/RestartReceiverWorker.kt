package com.wexberry.incomingcall.workers

import android.content.Context
import android.content.IntentFilter
import android.telephony.TelephonyManager
import androidx.work.*
import com.wexberry.incomingcall.receiver.PhoneStateChangedReceiver
import java.util.concurrent.TimeUnit


class RestartReceiverWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val receiver = PhoneStateChangedReceiver()
        val intentFilter = IntentFilter()

        intentFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
        applicationContext.registerReceiver(receiver, intentFilter)

        return Result.success() // Статус: выполнено
    }

    companion object {
        private const val RESTART_RECEIVER_WORKER_TAG = "SimpleWorkerTag"

        // 1. Метод для создания PeriodicWorkRequest с интервалом повторения 3 часа
        private fun createWorkRequest(data: Data): PeriodicWorkRequest {
            return PeriodicWorkRequest.Builder(RestartReceiverWorker::class.java, 3, TimeUnit.HOURS)
                .setInputData(data)
                .addTag(RESTART_RECEIVER_WORKER_TAG) // Устанавливаем TAG по которому мы можем запускать/останавливать задачу
                .build()
        }

        // 2. Метод для запуска задачи с политикой "KEEP - режим оставит в работе текущую выполняемую задачу. А новая будет проигнорирована"
        fun startWork(context: Context) {
            val work = createWorkRequest(Data.EMPTY)
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(RESTART_RECEIVER_WORKER_TAG, ExistingPeriodicWorkPolicy.KEEP, work)
        }

        // 3. Метод для остановки
        fun cancelWork(context: Context) {
            WorkManager.getInstance(context).cancelAllWorkByTag(RESTART_RECEIVER_WORKER_TAG)
        }
    }
}