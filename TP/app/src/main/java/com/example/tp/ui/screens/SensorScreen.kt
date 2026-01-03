package com.example.tp.ui.screens


import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EcraSensors(navController: NavController, experimentId: String) {

    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }

    // Sensors to monitor
    val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    // State for readings
    val accelValues = remember { mutableStateOf(listOf(0f, 0f, 0f)) }
    val lightValue = remember { mutableStateOf(0f) }
    val gyroValues = remember { mutableStateOf(listOf(0f, 0f, 0f)) }

    var isMeasuring by remember { mutableStateOf(false) }

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    // Sensor listener
    val sensorEventListener = remember {
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val timestamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(Date())
                val uid = auth.currentUser?.uid ?: "unknown"

                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> {
                        accelValues.value = event.values.toList()
                        // Save to Firestore
                        saveMeasurement(
                            db, experimentId, "ACCELEROMETER",
                            accelValues.value.joinToString(","), "m/s²", timestamp, uid
                        )
                    }
                    Sensor.TYPE_LIGHT -> {
                        lightValue.value = event.values.first()
                        saveMeasurement(
                            db, experimentId, "LIGHT",
                            lightValue.value.toString(), "lx", timestamp, uid
                        )
                    }
                    Sensor.TYPE_GYROSCOPE -> {
                        gyroValues.value = event.values.toList()
                        saveMeasurement(
                            db, experimentId, "GYROSCOPE",
                            gyroValues.value.joinToString(","), "rad/s", timestamp, uid
                        )
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text("Sensors Monitor", fontSize = 28.sp)

        Button(onClick = {
            if (!isMeasuring) {
                accelerometer?.let { sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_NORMAL) }
                lightSensor?.let { sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_NORMAL) }
                gyroscope?.let { sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_NORMAL) }
            } else {
                sensorManager.unregisterListener(sensorEventListener)
            }
            isMeasuring = !isMeasuring
        }) {
            Text(if (isMeasuring) "Stop Measuring" else "Start Measuring")
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Accelerometer: x=${accelValues.value[0]}, y=${accelValues.value[1]}, z=${accelValues.value[2]}")
            Text("Light: ${lightValue.value} lx")
            Text("Gyroscope: x=${gyroValues.value[0]}, y=${gyroValues.value[1]}, z=${gyroValues.value[2]}")
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(onClick = { navController.navigate("main") }) {
            Text("Return")
        }
    }
}

private fun saveMeasurement(
    db: FirebaseFirestore,
    experimentId: String,
    source: String,
    value: String,
    unit: String,
    timestamp: String,
    uid: String
) {
    val measurement = mapOf(
        "ts" to timestamp,
        "value" to value,
        "unit" to unit,
        "source" to source,
        "model" to android.os.Build.MODEL,
        "os" to "Android ${android.os.Build.VERSION.RELEASE}",
        "note" to "",
        "createdByUid" to uid
    )

    db.collection("experiments")
        .document(experimentId)
        .collection("measurements")
        .add(measurement)
        .addOnSuccessListener { docRef ->
            Log.d("Firestore", "Measurement added: ${docRef.id}")
        }
        .addOnFailureListener { e ->
            Log.w("Firestore", "Error adding measurement", e)
        }
}
