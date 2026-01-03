package com.example.tp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tp.auth.registerWithEmail
import com.google.firebase.auth.FirebaseAuth

@Composable
fun EcraCreateAccount(navController: NavController) {
    val auth = FirebaseAuth.getInstance()

    val name = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Create your account", fontSize = 30.sp)

        TextField(name.value, { name.value = it }, label = { Text("Name") })
        TextField(email.value, { email.value = it }, label = { Text("E-mail") })
        TextField(password.value, { password.value = it }, label = { Text("Password") })

        errorMessage.value?.let { Text(it) }

        Button(onClick = {
            registerWithEmail(
                auth,
                email.value,
                password.value,
                onSuccess = {
                    navController.navigate("main") {
                        popUpTo("registo") { inclusive = true }
                    }
                },
                onError = { errorMessage.value = it }
            )
        }) {
            Text("Sign in")
        }
    }
}
