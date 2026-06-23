package com.example.myapplication

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun HistoryScreen(context: Context, onBack: () -> Unit) {

    // ✅ CURRENT STORAGE RETURNS STRING (SAFE)
    val historyText = StorageHelper.getHistory(context)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF020817))
            .padding(16.dp)
    ) {

        // 🔝 TOP BAR WITH BACK BUTTON
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Button(onClick = onBack) {
                Text("⬅ Back")
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "📜 History",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 📜 SCROLLABLE HISTORY TEXT
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (historyText.isEmpty()) "No history available" else historyText,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}