package com.example.myapplication

import android.content.Context
import androidx.compose.runtime.*

@Composable
fun AppNavigation(context: Context) {

    // 🔄 SCREEN STATE
    var screen by remember { mutableStateOf("login") }

    when (screen) {

        // 🔐 LOGIN SCREEN
        "login" -> LoginScreen(
            context = context,
            onLogin = { screen = "dashboard" },
            onSignup = { screen = "signup" }
        )

        // 📝 SIGNUP SCREEN
        "signup" -> SignupScreen(
            context = context,
            onDone = { screen = "login" }
        )

        // 🏠 DASHBOARD (NO CONTEXT NEEDED NOW)
        "dashboard" -> DashboardScreen(
            onNavigate = { screen = it }
        )

        // 📜 HISTORY
        "history" -> HistoryScreen(
            context = context,
            onBack = { screen = "dashboard" }
        )

        // ⚙️ SETTINGS
        "settings" -> SettingsScreen(
            context = context,
            onBack = { screen = "dashboard" }
        )

        // 📡 SENSOR
        "sensor" -> SensorScreen(
            context = context,
            onBack = { screen = "dashboard" }
        )
    }
}