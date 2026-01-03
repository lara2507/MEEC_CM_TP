package com.example.tp.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tp.auth.signInWithEmail
import com.google.firebase.auth.FirebaseAuth

@Composable
fun EcraLogin(navController: NavController) {
    val auth = FirebaseAuth.getInstance()

    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome", fontSize = 30.sp)

        TextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("E-mail") }
        )

        TextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Password") }
        )

        errorMessage.value?.let { Text(it) }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = {
                signInWithEmail(
                    auth,
                    email.value,
                    password.value,
                    onSuccess = {
                        navController.navigate("main") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onError = { errorMessage.value = it }
                )
            }) {
                Text("Login")
            }

            Button(onClick = { navController.navigate("registo") }) {
                Text("Register")
            }
        }
    }
}
