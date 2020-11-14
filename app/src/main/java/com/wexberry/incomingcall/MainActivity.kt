package com.wexberry.incomingcall

import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.wexberry.incomingcall.receiver.PhoneStateChangedReceiver
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initFunc()
    }

    override fun onDestroy() {
        super.onDestroy()

        // Останавливаем сервис отображения окна
//        val intentMyService = Intent(this, MyService::class.java)
//        this.stopService(intentMyService)
    }

    private fun initFunc() {
        btnClick() // Обработчик нажатия кнопок
        registerReceived() // Динамическая регистрация Ресивера
        permissionStatus() // Запрос разрешения (Андроид 6+)
    }

    private fun btnClick() {
        btnMagic.setOnClickListener {
            // if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            //if (!Settings.canDrawOverlays(this)) {
            permissionOverlayOverWindowForXiaomiOppoVivo()
            //Toast.makeText(this, "Дайте разрешение", Toast.LENGTH_SHORT).show()
            //} else {
            //Toast.makeText(this, "Отключено", Toast.LENGTH_SHORT).show()
            //Запускаем сервис отображения окна
//                    val intentMyService = Intent(this, MyService::class.java)
//                    ContextCompat.startForegroundService(applicationContext, intentMyService)
            //}
            //}
        }

        btnPermission.setOnClickListener {
            // Запрашиваем разрешение на наложение окон
            permissionOverlayOverWindows()
        }

        btnPermission2.setOnClickListener {
            // Запрашиваем разрешение Read Call Log
            permissionReadCallLog()
        }
    }

    // Динамическая регистрация Ресивера
    private fun registerReceived() {
        val receiver = PhoneStateChangedReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
        registerReceiver(receiver, intentFilter)
    }

    private fun permissionOverlayOverWindowForXiaomiOppoVivo() {
        try {
            val intent = Intent()
            val manufacturer = Build.MANUFACTURER
            when {
                "xiaomi".equals(manufacturer, ignoreCase = true) -> {
                    intent.component = ComponentName(
                        "com.miui.securitycenter",
                        "com.miui.permcenter.autostart.AutoStartManagementActivity"
                    )
                }
                "oppo".equals(manufacturer, ignoreCase = true) -> {
                    intent.component = ComponentName(
                        "com.coloros.safecenter",
                        "com.coloros.safecenter.permission.startup.StartupAppListActivity"
                    )
                }
                "vivo".equals(manufacturer, ignoreCase = true) -> {
                    intent.component = ComponentName(
                        "com.vivo.permissionmanager",
                        "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
                    )
                }
            }
            val list: List<ResolveInfo> = this.packageManager
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            if (list.size > 0) {
                this.startActivity(intent)
            }
        } catch (e: Exception) {
            Log.d("Exception", e.toString())
        }
    }

    // Запрашиваем разрешение на наложение окон (такое разрешение можно запрашивать только так)
    private fun permissionOverlayOverWindows() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val intent =
                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivityForResult(intent, 0)
        }
    }

    private fun permissionReadCallLog() {
        val permissionStatus: Int = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_CALL_LOG
        )
        // Если разрешения не дано, то запрашиваем его
        if (permissionStatus == PackageManager.PERMISSION_DENIED) {
            // Список с разрешениями
            val permissions = arrayOf(
                android.Manifest.permission.READ_CALL_LOG
            )
            // Запрашиваем разрешения из списка
            ActivityCompat.requestPermissions(this, permissions, 0)
        }
    }

    // Запрашиваем разрешение на доступ к звонкам с Андроид 6+ (М)
    private fun permissionStatus() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val permissionStatus: Int = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_PHONE_STATE
            )

            // Если разрешения не дано, то запрашиваем его
            if (permissionStatus == PackageManager.PERMISSION_DENIED) {
                // Список с разрешениями
                val permissions = arrayOf(
                    android.Manifest.permission.READ_PHONE_STATE
                )
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
}