package com.example.tp.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tp.sensors.AccelerometerSensor
import com.example.tp.sensors.BarometerSensor
import com.example.tp.sensors.LightSensor
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.sqrt

@Composable
fun EcraSensors(navController: NavController, experimentId: String) {
    val context = LocalContext.current

    var selectedSensor by remember { mutableStateOf<String?>(null) }

    // Sensor states
    var lightValue by remember { mutableStateOf(0f) }
    var pressureValue by remember { mutableStateOf(0f) }

    val accelSensor = remember { AccelerometerSensor(context) }
    val accelState = accelSensor.accelValues.collectAsState(initial = Triple(0f, 0f, 0f))

    val lightSensor = remember { LightSensor(context) }
    val barometerSensor = remember { BarometerSensor(context) }

    val db = FirebaseFirestore.getInstance()

    // Function to save measurement to Firestore
    fun saveMeasurement(value: Float, unit: String) {
        val measurement = mapOf(
            "ts" to System.currentTimeMillis(),
            "value" to value,
            "unit" to unit,
            "source" to "PHONE_SENSOR",
            "model" to android.os.Build.MODEL,
            "os" to "Android ${android.os.Build.VERSION.SDK_INT}",
            "note" to ""
        )

        db.collection("experiments")
            .document(experimentId)
            .collection("measurements")
            .add(measurement)
    }

    // Start/stop sensors
    DisposableEffect(selectedSensor) {
        // Stop all sensors first
        accelSensor.stop()
        lightSensor.stop()
        barometerSensor.stop()

        when (selectedSensor) {
            "Light" -> {
                lightSensor.onLightChanged = { lux ->
                    lightValue = lux
                    saveMeasurement(lux, "lx")
                }
                lightSensor.start()
            }

            "Accelerometer" -> {
                accelSensor.start()
            }

            "Barometer" -> {
                barometerSensor.onPressureChanged = { pressure ->
                    pressureValue = pressure
                    saveMeasurement(pressure, "hPa")
                }
                barometerSensor.start()
            }
        }

        onDispose {
            accelSensor.stop()
            lightSensor.stop()
            barometerSensor.stop()
        }
    }

    // Accelerometer readings handled in composable scope
    if (selectedSensor == "Accelerometer") {
        LaunchedEffect(accelState.value) {
            val (x, y, z) = accelState.value
            val magnitude = sqrt(x * x + y * y + z * z)
            saveMeasurement(magnitude, "m/s²")
        }
    }

    // UI
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Select a Sensor", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf("Light", "Accelerometer", "Barometer").forEach { sensorName ->
                Button(
                    onClick = { selectedSensor = sensorName },
                    enabled = selectedSensor != sensorName
                ) { Text(sensorName) }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        when (selectedSensor) {
            "Light" -> Text("Light: $lightValue lx", fontSize = 16.sp)
            "Accelerometer" -> {
                val (x, y, z) = accelState.value
                Text(
                    "Accelerometer magnitude: ${"%.2f".format(sqrt(x*x + y*y + z*z))} m/s²",
                    fontSize = 16.sp
                )
            }
            "Barometer" -> Text("Pressure: $pressureValue hPa", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text("Return")
        }
    }
}

