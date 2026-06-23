package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreen(onNavigate: (String) -> Unit) {

    // ✅ GET CONTEXT SAFELY
    val context = LocalContext.current

    val sensor = remember { SensorManagerHelper(context) }

    var eps by remember { mutableIntStateOf(0) }

    // 🔥 PREVENT MULTIPLE ALERT TRIGGERS
    var alertStarted by remember { mutableStateOf(false) }

    // 🚀 START SENSOR
    LaunchedEffect(Unit) {
        sensor.start(
            onUpdate = { value ->
                eps = value

                // 🔥 AUTO TRIGGER (ONLY ONCE)
                if (eps > 50 && !alertStarted) {
                    alertStarted = true
                    AlertHelper.startContinuousAlert(context)
                }
            },
            onEmergency = {
                if (!alertStarted) {
                    alertStarted = true
                    AlertHelper.startContinuousAlert(context)
                }
            }
        )
    }

    // 🛑 STOP SENSOR WHEN LEAVING
    DisposableEffect(Unit) {
        onDispose {
            sensor.stop()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF020817), Color(0xFF1E293B))
                )
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Card {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 🔥 LOGIN TIME (ADD HERE)
                Text(
                    text = StorageHelper.getLastLogin(context),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(10.dp))

                // 📊 EPS TITLE
                Text(
                    "EPS SCORE",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 📊 EPS VALUE
                Text(
                    "$eps%",
                    color = when {
                        eps > 80 -> Color.Red
                        eps > 50 -> Color.Yellow
                        else -> Color.Green
                    },
                    style = MaterialTheme.typography.headlineLarge
                )

                Spacer(modifier = Modifier.height(20.dp))

                // ⬅ BACK
                Button(
                    onClick = { onNavigate("login") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("⬅ Back")
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 🚨 EMERGENCY
                Button(
                    onClick = {
                        alertStarted = true
                        AlertHelper.startContinuousAlert(context)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🚨 Emergency")
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 📜 HISTORY
                Button(
                    onClick = { onNavigate("history") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("📜 History")
                }

                // 📡 SENSOR
                Button(
                    onClick = { onNavigate("sensor") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("📡 Sensor")
                }

                // ⚙️ SETTINGS
                Button(
                    onClick = { onNavigate("settings") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("⚙️ Settings")
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 🛑 STOP ALERT
                Button(
                    onClick = {
                        alertStarted = false
                        AlertHelper.stopAlert(context)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("🛑 Stop Alert")
                }
            }
        }
    }
}