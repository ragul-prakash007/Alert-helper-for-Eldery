package com.example.myapplication

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(context: Context, onBack: () -> Unit) {

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

        Text("⚙️ Settings", style = MaterialTheme.typography.headlineLarge, color = Color.White)

        Spacer(Modifier.height(20.dp))

        Card {
            Column(Modifier.padding(20.dp)) {

                Button(onClick = {
                    // Clear user data
                    val prefs = context.getSharedPreferences("user", Context.MODE_PRIVATE)
                    prefs.edit().clear().apply()
                }) {
                    Text("Reset User Data")
                }

                Spacer(Modifier.height(10.dp))

                Button(onClick = {
                    // Clear history
                    val prefs = context.getSharedPreferences("history", Context.MODE_PRIVATE)
                    prefs.edit().clear().apply()
                }) {
                    Text("Clear History")
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Button(onClick = onBack) {
            Text("⬅ Back")
        }
    }
}