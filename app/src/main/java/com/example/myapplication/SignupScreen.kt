package com.example.myapplication

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun SignupScreen(context: Context, onDone: () -> Unit) {

    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }

    var h1name by remember { mutableStateOf("") }
    var h1phone by remember { mutableStateOf("") }

    var h2name by remember { mutableStateOf("") }
    var h2phone by remember { mutableStateOf("") }

    var h3name by remember { mutableStateOf("") }
    var h3phone by remember { mutableStateOf("") }

    var message by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF020817), Color(0xFF1E293B))
                )
            )
            .verticalScroll(scrollState),   // 🔥 SCROLL ENABLED
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(40.dp))

        Card {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(0.9f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text("Signup", style = MaterialTheme.typography.headlineMedium)

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(name, { name = it }, label = { Text("Full Name") })
                OutlinedTextField(age, { age = it }, label = { Text("Age") })
                OutlinedTextField(username, { username = it }, label = { Text("Username") })

                OutlinedTextField(
                    pass,
                    { pass = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation()
                )

                OutlinedTextField(
                    confirm,
                    { confirm = it },
                    label = { Text("Confirm Password") },
                    visualTransformation = PasswordVisualTransformation()
                )

                Spacer(Modifier.height(10.dp))
                Text("Emergency Helpers")

                OutlinedTextField(h1name, { h1name = it }, label = { Text("Helper1 Name") })
                OutlinedTextField(h1phone, { h1phone = it }, label = { Text("Helper1 Phone") })

                OutlinedTextField(h2name, { h2name = it }, label = { Text("Helper2 Name") })
                OutlinedTextField(h2phone, { h2phone = it }, label = { Text("Helper2 Phone") })

                OutlinedTextField(h3name, { h3name = it }, label = { Text("Helper3 Name") })
                OutlinedTextField(h3phone, { h3phone = it }, label = { Text("Helper3 Phone") })

                Spacer(Modifier.height(20.dp))

                // 💾 SAVE DETAILS
                Button(
                    onClick = {

                        if (name.isEmpty() || username.isEmpty() || pass.isEmpty()) {
                            message = "❌ Fill all fields"
                            return@Button
                        }

                        if (pass != confirm) {
                            message = "❌ Password mismatch"
                            return@Button
                        }

                        val contacts = listOf(
                            Contact(h1name, h1phone),
                            Contact(h2name, h2phone),
                            Contact(h3name, h3phone)
                        )

                        StorageHelper.saveUser(
                            context,
                            username,
                            pass,
                            name,
                            age,
                            contacts
                        )

                        message = "✅ Details Saved Successfully"
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("💾 Save Details")
                }

                Spacer(Modifier.height(10.dp))

                // 🚀 CONTINUE
                Button(
                    onClick = { onDone() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("➡ Continue")
                }

                Spacer(Modifier.height(10.dp))

                Text(
                    message,
                    color = if (message.contains("✅")) Color.Green else Color.Red
                )
            }
        }

        Spacer(Modifier.height(40.dp))
    }
}