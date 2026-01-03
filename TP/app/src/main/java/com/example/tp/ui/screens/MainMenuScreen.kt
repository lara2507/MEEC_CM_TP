package com.example.tp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tp.viewmodel.ExperimentViewModel
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun EcraMain(navController: NavController, viewModel: ExperimentViewModel = viewModel()) {
    val experiments = remember { mutableStateOf<List<Map<String, Any?>>>(emptyList()) }

    // Load experiments from Firestore
    LaunchedEffect(Unit) {
        FirebaseFirestore.getInstance()
            .collection("experiments")
            .get()
            .addOnSuccessListener { result ->
                val loaded = result.map { doc ->
                    val tagsList = (doc.get("tags") as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()
                    val photosList = (doc.get("photos") as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()

                    mapOf(
                        "id" to doc.id,
                        "title" to doc.get("title")?.toString().orEmpty(),
                        "objective" to doc.get("objective")?.toString().orEmpty(),
                        "date" to doc.get("date")?.toString().orEmpty(),
                        "tags" to tagsList,
                        "photos" to photosList,
                        "createdByUid" to doc.get("createdByUid")?.toString().orEmpty()
                    )
                }
                experiments.value = loaded
            }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Main Menu", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))

        // Section: Add Experiment
        Button(onClick = { navController.navigate("add_experiments") }) {
            Text("Add Experiment")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section: Existing Experiments
        Text("Your Experiments", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(experiments.value) { exp ->
                val experimentId = exp["id"] as? String ?: return@items
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text("Title: ${exp["title"]}")
                    Text("Objective: ${exp["objective"]}")
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(onClick = { navController.navigate("sensors/$experimentId") }) {
                            Text("Measure Sensors")
                        }
                        Button(onClick = { navController.navigate("visualize/$experimentId") }) {
                            Text("View Data")
                        }
                    }
                }
            }
        }
    }
}




