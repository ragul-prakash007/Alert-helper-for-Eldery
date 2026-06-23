package com.example.myapplication

import android.content.Context
import android.hardware.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.dp

@Composable
fun SensorScreen(context: Context, onBack: () -> Unit) {

    var x by remember { mutableStateOf(0f) }
    var y by remember { mutableStateOf(0f) }
    var z by remember { mutableStateOf(0f) }

    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    val listener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            x = event.values[0]
            y = event.values[1]
            z = event.values[2]
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    LaunchedEffect(Unit) {
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(Color.Black, Color.DarkGray))
            )
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text("📡 Sensor Monitor", style = MaterialTheme.typography.headlineLarge, color = Color.White)

        Spacer(Modifier.height(20.dp))

        Card {
            Column(Modifier.padding(20.dp)) {
                Text("X: ${x.toInt()}")
                Text("Y: ${y.toInt()}")
                Text("Z: ${z.toInt()}")
            }
        }

        Spacer(Modifier.height(20.dp))

        Button(onClick = onBack) {
            Text("⬅ Back")
        }
    }
}