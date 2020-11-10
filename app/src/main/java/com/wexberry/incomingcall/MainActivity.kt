package com.wexberry.incomingcall

import android.app.KeyguardManager
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.wexberry.incomingcall.receiver.PhoneStateChangedReceiver
import com.wexberry.incomingcall.service.MyService
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    lateinit var myService: MyService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val keyguardManager: KeyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            keyguardManager.requestDismissKeyguard(this, null)
        }

        init()
        initFunc()
    }

    override fun onDestroy() {
        super.onDestroy()

        // Останавливаем сервис отображения окна
        val intentMyService = Intent(this, MyService::class.java)
        this.stopService(intentMyService)
    }

    private fun init() {
        myService = MyService()
    }

    private fun initFunc() {
        myService
        btnClick() // Обработчик нажатия кнопок
        registerReceived() // Динамическая регистрация Ресивера
        permissionStatus() // Запрос разрешения (Андроид 6+)
    }

    private fun btnClick() {
        btnMagic.setOnClickListener {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    Toast.makeText(this, "Дайте разрешение", Toast.LENGTH_SHORT).show()
                } else {
                    // Запускаем сервис отображения окна
                    val intentMyService = Intent(this, MyService::class.java)
                    this.startService(intentMyService)
                }
            }
        }

        btnPermission.setOnClickListener {
            // Зарпашиваем разрешение на наложение окон
            permissionOverlayOverWindows()
        }
    }

    // Динамическая регистрация Ресивера
    private fun registerReceived() {
        val receiver = PhoneStateChangedReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
        registerReceiver(receiver, intentFilter)
    }

    private fun permissionOverlayOverWindows() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val intent =
                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivityForResult(intent, 0)
        }
    }

    // Запрашиваем разрешения с Андроид 6+ (М)
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
                    android.Manifest.permission.READ_PHONE_STATE,
                    android.Manifest.permission.DISABLE_KEYGUARD
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
                myService
            } else {
                Toast.makeText(this, "Зря ты так сделал..", Toast.LENGTH_SHORT).show()
            }
        }
    }
}