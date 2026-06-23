package com.example.myapplication

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.Looper
import kotlin.math.abs
import kotlin.math.sqrt

class SensorManagerHelper(private val context: Context) : SensorEventListener {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager

    private val accelerometer: Sensor? =
        sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var callback: ((Int) -> Unit)? = null
    private var emergencyCallback: (() -> Unit)? = null

    private var lastMagnitude = 0f   // ✅ FLOAT FIX
    private var isFallDetected = false
    private var isTriggered = false
    private var isRegistered = false

    private val handler = Handler(Looper.getMainLooper())

    // 🚀 START SENSOR
    fun start(
        onUpdate: (Int) -> Unit,
        onEmergency: () -> Unit
    ) {
        callback = onUpdate
        emergencyCallback = onEmergency

        if (!isRegistered) {
            accelerometer?.let {
                sensorManager?.registerListener(
                    this,
                    it,
                    SensorManager.SENSOR_DELAY_GAME
                )
                isRegistered = true
            }
        }
    }

    // 📡 SENSOR DATA
    override fun onSensorChanged(event: SensorEvent) {

        if (event.values.size < 3) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        // ✅ FIXED FLOAT CALCULATION
        val magnitude = sqrt((x * x + y * y + z * z).toDouble()).toFloat()

        // 🔥 IMPACT
        val impact = (magnitude * 6).coerceAtMost(100f)

        // 🔥 INSTABILITY
        val instability = abs(magnitude - lastMagnitude) * 15f
        lastMagnitude = magnitude

        // 🧠 EPS SCORE
        var eps = (impact * 0.7f + instability * 0.3f)
            .toInt()
            .coerceIn(0, 100)

        callback?.invoke(eps)

        // 🚨 FALL DETECTION (VERY SENSITIVE)
        if (magnitude > 7 && !isFallDetected && !isTriggered) {

            isFallDetected = true

            handler.postDelayed({

                // 🧍 INACTIVITY CHECK
                if (lastMagnitude < 3 && !isTriggered) {

                    isTriggered = true

                    eps = 95
                    callback?.invoke(eps)

                    emergencyCallback?.invoke()
                }

                isFallDetected = false

            }, 2000)
        }

        // 🟢 RECOVERY DETECTION
        if (isTriggered && magnitude > 10) {
            isTriggered = false
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    // 🛑 STOP SENSOR
    fun stop() {
        if (isRegistered) {
            sensorManager?.unregisterListener(this)
            isRegistered = false
        }
    }
}