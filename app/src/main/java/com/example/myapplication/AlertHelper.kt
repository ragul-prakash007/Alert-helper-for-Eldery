@file:Suppress("DEPRECATION")

package com.example.myapplication

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.telephony.SmsManager
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.google.android.gms.location.*
import java.util.*

object AlertManager {

    private var isRunning = false
    private var alertCount = 0
    private const val MAX_ALERTS = 5

    private val handler = Handler(Looper.getMainLooper())
    private var tts: TextToSpeech? = null

    // ✅ REAL LIVE LOCATION FUNCTION (BEST)
    private fun getLiveLocation(context: Context, callback: (String) -> Unit) {

        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(context)

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 2000
        ).build()

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            callback("⚠ Permission not granted")
            return
        }

        fusedLocationClient.requestLocationUpdates(
            request,
            object : LocationCallback() {

                override fun onLocationResult(result: LocationResult) {

                    val loc = result.lastLocation

                    if (loc != null) {

                        val link =
                            "https://maps.google.com/?q=${loc.latitude},${loc.longitude}"

                        callback(link)

                        fusedLocationClient.removeLocationUpdates(this)
                    } else {
                        callback("⚠ Location unavailable")
                    }
                }
            },
            Looper.getMainLooper()
        )
    }

    fun startContinuousAlert(context: Context) {

        if (isRunning) return

        isRunning = true
        alertCount = 0

        val contacts = StorageHelper.getContacts(context)
        val name = StorageHelper.getName(context)
        val age = StorageHelper.getAge(context)

        // ✅ TTS INIT
        tts = TextToSpeech(context) {
            tts?.language = Locale.US
        }

        fun sendSms(phone: String, message: String) {
            try {
                val smsManager = context.getSystemService(SmsManager::class.java)
                val parts = smsManager.divideMessage(message)

                smsManager.sendMultipartTextMessage(
                    phone,
                    null,
                    parts,
                    null,
                    null
                )
            } catch (_: Exception) {}
        }

        fun sendAlert() {

            // 🛑 STOP CONDITION
            if (!isRunning || alertCount >= MAX_ALERTS) {
                stopAlert()
                return
            }

            alertCount++

            // ✅ GET LIVE LOCATION FIRST
            getLiveLocation(context) { location ->

                val message = """
🚨 EMERGENCY ALERT 🚨

👤 Name: $name
🎂 Age: $age

📍 Live Location:
$location

⚠ Immediate help needed!
""".trimIndent()

                contacts.forEach { contact ->

                    val phone = contact.phone

                    // ✅ SMS
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.SEND_SMS
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        sendSms(phone, message)
                    }

                    // ✅ CALL
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CALL_PHONE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        val intent = Intent(Intent.ACTION_CALL)
                        intent.data = "tel:$phone".toUri()
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    }

                    // ✅ VOICE ALERT
                    tts?.speak(
                        "$name is in danger. Please help immediately.",
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        null
                    )

                    // ✅ SAVE HISTORY
                    StorageHelper.saveHistory(context, contact.name, phone)
                }
            }

            // 🔁 REPEAT ONLY 5 TIMES
            handler.postDelayed({ sendAlert() }, 7000)
        }

        sendAlert()
    }

    fun stopAlert() {
        isRunning = false
        alertCount = 0
        handler.removeCallbacksAndMessages(null)
        tts?.stop()
    }
}