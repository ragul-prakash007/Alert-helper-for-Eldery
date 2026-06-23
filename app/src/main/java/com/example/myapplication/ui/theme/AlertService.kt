package com.example.myapplication

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.*
import android.speech.tts.TextToSpeech
import android.telephony.SmsManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import java.util.*

/* ─────────────────────────────
   ALERT HELPER
───────────────────────────── */
object AlertHelper {

    fun startContinuousAlert(context: Context) {
        val intent = Intent(context, AlertService::class.java)
        ContextCompat.startForegroundService(context, intent)
    }

    fun stopAlert(context: Context) {
        context.stopService(Intent(context, AlertService::class.java))
    }
}

/* ─────────────────────────────
   ALERT SERVICE
───────────────────────────── */
class AlertService : Service() {

    companion object {
        private const val CHANNEL_ID = "alert_channel"
        private const val NOTIF_ID = 1
        private const val INTERVAL = 7000L
    }

    private val handler = Handler(Looper.getMainLooper())
    private var tts: TextToSpeech? = null
    private var location: Location? = null
    private var running = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createChannel()
        startForeground(NOTIF_ID, buildNotification())
        initTTS()
        startLocationUpdates()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!running) {
            running = true
            handler.post(loop)
        }
        return START_STICKY
    }

    override fun onDestroy() {
        running = false
        handler.removeCallbacksAndMessages(null)
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }

    /* 🔔 Notification */
    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Emergency",
                NotificationManager.IMPORTANCE_HIGH
            )
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("🚨 Emergency Active")
            .setContentText("Sending alerts...")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setOngoing(true)
            .build()
    }

    /* 🔊 TTS */
    private fun initTTS() {
        tts = TextToSpeech(this) {
            tts?.language = Locale.US
        }
    }

    private fun speak(msg: String) {
        tts?.speak(msg, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    /* 📍 Location */
    private fun startLocationUpdates() {

        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        val listener = object : LocationListener {
            override fun onLocationChanged(loc: Location) {
                location = loc
            }
        }

        lm.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            2000L,
            1f,
            listener
        )
    }

    private fun getLocation(): String {
        return if (location != null) {
            "https://maps.google.com/?q=${location!!.latitude},${location!!.longitude}"
        } else {
            "⚠ Turn on GPS"
        }
    }

    /* 📩 SMS */
    private fun sendSms(phone: String, message: String) {
        try {
            val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                getSystemService(SmsManager::class.java)
            } else {
                @Suppress("DEPRECATION")
                SmsManager.getDefault()
            }

            val parts = smsManager.divideMessage(message)
            smsManager.sendMultipartTextMessage(phone, null, parts, null, null)

        } catch (_: Exception) {
            // fallback
            startActivity(Intent(Intent.ACTION_SENDTO).apply {
                data = "smsto:$phone".toUri()
                putExtra("sms_body", message)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }

    /* 📞 CALL */
    private fun call(phone: String) {
        try {
            startActivity(Intent(Intent.ACTION_CALL).apply {
                data = "tel:$phone".toUri()
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (_: Exception) {}
    }

    /* 🔁 LOOP */
    private val loop = object : Runnable {
        override fun run() {
            if (!running) return
            sendAlert()
            handler.postDelayed(this, INTERVAL)
        }
    }

    /* 🚨 ALERT */
    private fun sendAlert() {

        val contacts = StorageHelper.getContacts(this)
        val name = StorageHelper.getName(this)
        val loc = getLocation()

        val msg = """
🚨 EMERGENCY 🚨
$name needs help!

📍 Location:
$loc
""".trimIndent()

        contacts.forEach {
            sendSms(it.phone, msg)
            call(it.phone)
            speak("$name is in danger")
            StorageHelper.saveHistory(this, it.name, it.phone)
        }
    }
}