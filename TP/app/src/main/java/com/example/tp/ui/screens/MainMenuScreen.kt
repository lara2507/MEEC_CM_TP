package com.example.tp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun EcraMain(navController: NavController) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Select one of the options", fontSize = 20.sp)

        Button(onClick = { navController.navigate("experiments") }) {
            Text("Experiments")
        }

        Button(onClick = { navController.navigate("sensors") }) {
            Text("Sensors")
        }

        Button(onClick = { navController.navigate("visualize") }) {
            Text("Visualize Data")
        }
    }
}


