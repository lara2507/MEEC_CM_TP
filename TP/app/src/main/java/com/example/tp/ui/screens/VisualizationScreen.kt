package com.example.tp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path


@Composable
fun EcraVisualization(navController: NavController, experimentId: String) {
    val db = FirebaseFirestore.getInstance()
    var measurements by remember { mutableStateOf<List<Map<String, Any?>>>(emptyList()) }

    // Load measurements
    LaunchedEffect(experimentId) {
        db.collection("experiments")
            .document(experimentId)
            .collection("measurements")
            .orderBy("ts")
            .get()
            .addOnSuccessListener { result ->
                val loaded = result.map { doc ->
                    val rawTs = doc.get("ts")
                    val ts = when (rawTs) {
                        is Long -> rawTs
                        is Double -> rawTs.toLong()
                        else -> 0L
                    }
                    val rawValue = doc.get("value")
                    val value = when (rawValue) {
                        is Double -> rawValue
                        is Long -> rawValue.toDouble()
                        else -> 0.0
                    }
                    mapOf(
                        "ts" to ts,
                        "value" to value,
                        "unit" to doc.get("unit")?.toString().orEmpty(),
                        "source" to doc.get("source")?.toString().orEmpty(),
                        "note" to doc.get("note")?.toString().orEmpty()
                    )
                }
                measurements = loaded
            }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Experiment Measurements", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))

        if (measurements.isNotEmpty()) {
            // Chart
            LineChart(
                data = measurements.map { it["value"] as Double },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(measurements) { m ->
                val ts = m["ts"] as Long
                val date = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                    .format(Date(ts))
                val value = m["value"]
                val unit = m["unit"]
                val source = m["source"]
                val note = m["note"]

                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text("Time: $date")
                    Text("Value: $value $unit")
                    Text("Source: $source")
                    if (note.toString().isNotEmpty()) Text("Note: $note")
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text("Return")
        }
    }
}

@Composable
fun LineChart(data: List<Double>, modifier: Modifier = Modifier) {
    if (data.isEmpty()) return

    val max = data.maxOrNull() ?: 1.0
    val min = data.minOrNull() ?: 0.0

    Canvas(modifier = modifier) {
        val path = Path()
        val widthPerPoint = size.width / (data.size - 1).coerceAtLeast(1)

        data.forEachIndexed { index, value ->
            val normalized = ((value - min) / (max - min)).toFloat()
            val x = index * widthPerPoint
            val y = size.height * (1 - normalized)

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        drawPath(path, color = Color.Blue, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f))
    }
}



