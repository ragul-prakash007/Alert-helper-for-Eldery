package com.example.myapplication

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(context: Context, onLogin: () -> Unit, onSignup: () -> Unit) {

    var user by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(Color.Black, Color.DarkGray))
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Card {
            Column(Modifier.padding(20.dp)) {

                Text("Login", style = MaterialTheme.typography.headlineMedium)

                OutlinedTextField(
                    value = user,
                    onValueChange = { user = it },
                    label = { Text("Username") }
                )

                OutlinedTextField(
                    value = pass,
                    onValueChange = { pass = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation()
                )

                Spacer(Modifier.height(10.dp))

                Button(
                    onClick = {
                        if (StorageHelper.validateLogin(context, user, pass)) {
                            error = false
                            onLogin()
                        } else {
                            error = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Login")
                }

                if (error) {
                    Text("Invalid Login", color = Color.Red)
                }

                Spacer(Modifier.height(10.dp))

                TextButton(onClick = onSignup) {
                    Text("Don't have an account? Signup")
                }
            }
        }
    }
}