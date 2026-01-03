package com.example.tp.ui.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tp.viewmodel.ExperimentViewModel
import com.google.firebase.firestore.FirebaseFirestore

@Composable

fun EcraExperiments(navController: NavController) {
    Column(
        Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Experiments",
            fontSize = 30.sp,
        )
        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Button(
            onClick = { navController.navigate("add_experiments") },
        ) {
            Text("Add Experiment")
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )
        Button(
            onClick = { navController.navigate("manage_experiments") },
        ) {
            Text("Manage Experiments")
        }
        Spacer(
            modifier = Modifier.height(10.dp)
        )
        Button(
            onClick = { navController.navigate("main") },
        ) {
            Text("Return")
        }
        Spacer(
            modifier = Modifier.height(10.dp)
        )
    }
}

@Composable
fun EcraAddExperiments(
    navController: NavController,
    viewModel: ExperimentViewModel = viewModel()
) {
    // UI state
    var exp_title = remember { mutableStateOf("") }
    var exp_objective = remember { mutableStateOf("") }
    var exp_date = remember { mutableStateOf("") }
    var exp_tagInput = remember { mutableStateOf("") }
    var exp_tags = remember { mutableStateOf(listOf<String>()) }
    var exp_photos = remember { mutableStateOf(listOf<String>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(text = "Add your Experiment", fontSize = 30.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Insert your experiment-related information.", fontSize = 14.sp)
        Spacer(modifier = Modifier.height(10.dp))

        // Title
        TextField(
            value = exp_title.value,
            label = { Text("Title") },
            onValueChange = { exp_title.value = it },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Objective
        TextField(
            value = exp_objective.value,
            label = { Text("Objective") },
            onValueChange = { exp_objective.value = it },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Date
        TextField(
            value = exp_date.value,
            onValueChange = {
                if (it.matches(Regex("[0-9.]*")) && it.length <= 10) {
                    exp_date.value = it
                }
            },
            label = { Text("Date (DD.MM.YYYY)") },
            placeholder = { Text("DD.MM.YYYY") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Tag input
        TextField(
            value = exp_tagInput.value,
            onValueChange = { exp_tagInput.value = it },
            label = { Text("Add Tag") },
            placeholder = { Text("Enter a tag") },
            trailingIcon = {
                IconButton(
                    onClick = {
                        val trimmed = exp_tagInput.value.trim()
                        if (trimmed.isNotEmpty() && !exp_tags.value.contains(trimmed)) {
                            exp_tags.value = exp_tags.value + trimmed
                            exp_tagInput.value = ""
                        }
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add tag")
                }
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Buttons
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = {
                    viewModel.addExperiment(
                        title = exp_title.value,
                        objective = exp_objective.value,
                        date = exp_date.value,
                        tags = exp_tags.value,
                        photos = exp_photos.value,
                        createdByUid = "abc123" // Replace with FirebaseAuth UID
                    )
                    navController.navigate("experiments")
                }
            ) {
                Text("Add Experiment")
            }

            Button(
                onClick = { navController.navigate("experiments") }
            ) {
                Text("Return")
            }
        }

        // Display tags
        if (exp_tags.value.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))
            Text("Tags: ${exp_tags.value.joinToString(", ")}")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EcraManageExperiments(navController: NavController) {

    val experiments = remember { mutableStateOf<List<Map<String, Any?>>>(emptyList()) }

    // Load experiments from Firestore
    LaunchedEffect(Unit) {
        FirebaseFirestore.getInstance()
            .collection("experiments")
            .get()
            .addOnSuccessListener { result ->
                val loaded = result.map { document ->
                    val title = document.get("title")?.toString() ?: ""
                    val objective = document.get("objective")?.toString() ?: ""
                    val date = document.get("date")?.toString() ?: ""
                    val createdByUid = document.get("createdByUid")?.toString() ?: ""
                    val tagsList =
                        (document.get("tags") as? List<*>)?.map { it.toString() } ?: emptyList()
                    val photosList =
                        (document.get("photos") as? List<*>)?.map { it.toString() } ?: emptyList()

                    mapOf(
                        "title" to title,
                        "objective" to objective,
                        "date" to date,
                        "tags" to tagsList,
                        "photos" to photosList,
                        "createdByUid" to createdByUid
                    )
                }
                experiments.value = loaded
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting documents.", exception)
            }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text(
            text = "My Experiments",
            fontSize = 28.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(experiments.value) { exp ->

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = "Title: ${exp["title"] as? String ?: "No title"}",
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Objective: ${exp["objective"] as? String ?: "No objective"}",
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Date: ${exp["date"] as? String ?: "Unknown"}",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Tags: ${(exp["tags"] as? List<*>)?.joinToString(", ") ?: "-"}",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Photos: ${(exp["photos"] as? List<*>)?.joinToString(", ") ?: "-"}",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Created by: ${exp["createdByUid"] as? String ?: "Unknown"}",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider() // separates each experiment nicely
                }
            }
        }
    }
}

