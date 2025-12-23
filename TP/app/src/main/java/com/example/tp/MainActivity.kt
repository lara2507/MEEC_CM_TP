package com.example.tp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tp.ui.theme.TPTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TPTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavegacaoApp()
                }
            }
        }
    }
}
@Composable
fun NavegacaoApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            EcraLogin(navController)
        }

        composable("registo") {
            EcraCreateAccount(navController)
        }

        composable("main"){
            EcraMain(navController)
        }
        composable("experiments"){
            EcraExperiments(navController)
        }
        composable("sensors"){
            EcraSensors(navController)
        }
        composable("visualize"){
            EcraVisualization(navController)
        }
        composable("add_experiments"){
            EcraAddExperiments(navController)
        }
        composable("manage_experiments"){
            EcraManageExperiments(navController)
        }
    }
}

@Composable
fun EcraLogin(navController: NavController) {

    val auth = FirebaseAuth.getInstance()

    var email = remember{ mutableStateOf("") }
    var password = remember { mutableStateOf("") }
    var errorMessage = remember { mutableStateOf<String?>(null) }

    Column(Modifier
        .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center){
        Text(
            text = "Welcome",
            fontSize = 30.sp,
        )
        Spacer(
            modifier= Modifier.height(10.dp)
        )
        Text(
            text = "Insert your e-mail",
            fontSize = 10.sp,
        )
        TextField(
            value= email.value,
            label= {Text("E-mail")},
            onValueChange = {email.value = it}
        )
        Spacer(
            modifier= Modifier.height(10.dp)
        )
        Text(
            text = "Insert your password",
            fontSize = 10.sp,
        )
        TextField(
            value= password.value,
            label= {Text("Password")},
            onValueChange = {password.value = it}
        )
        Spacer(
            modifier= Modifier.height(10.dp)
        )

        errorMessage.value?.let {
            Text(text = it)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp))
        {
            Button(
                onClick = {
                    signInWithEmail(
                        auth = auth,
                        email = email.value,
                        password = password.value,
                        onSuccess = {
                            navController.navigate("main") {
                                popUpTo("login") { inclusive = true }
                            }
                        },
                        onError = { error ->
                            errorMessage.value = error
                        }
                    )
                }
            ) {
                Text("Login")
            }

            Button(
                onClick = { navController.navigate("registo") },
            ) {
                Text("Register")
            }
    }

    }
}

fun signInWithEmail(
    auth: FirebaseAuth,
    email: String,
    password: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    if (email.isBlank() || password.isBlank()) {
        onError("Email and password are empty. Please introduce them again.")
        return
    }

    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSuccess()
            } else {
                onError(task.exception?.localizedMessage ?: "Login failed")
            }
        }
}
fun registerWithEmail(
    auth: FirebaseAuth,
    email: String,
    password: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    if (email.isBlank() || password.length < 6) {
        onError("Email cannot be empty and password must be at least 6 characters")
        return
    }

    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSuccess()
            } else {
                onError(task.exception?.localizedMessage ?: "Registration failed")
            }
        }
}


@Composable
fun EcraCreateAccount(navController: NavController) {

    val auth = FirebaseAuth.getInstance()

    var name = remember{ mutableStateOf("") }
    var email = remember{ mutableStateOf("") }
    var password = remember { mutableStateOf("") }
    var errorMessage = remember { mutableStateOf<String?>(null) }
    Column(Modifier
        .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center){
        Text(
            text = "Create your account",
            fontSize = 30.sp,
        )
        Spacer(
            modifier= Modifier.height(10.dp)
        )
        Text(
            text = "Insert your name"
        )
        TextField(
            value= name.value,
            label= {Text("Name")},
            onValueChange = {name.value = it}
        )
        Spacer(
            modifier= Modifier.height(10.dp)
        )
        Text(
            text = "Insert your e-mail",
            fontSize = 10.sp,
        )
        TextField(
            value= email.value,
            label= {Text("E-mail")},
            onValueChange = {email.value = it}
        )
        Spacer(
            modifier= Modifier.height(10.dp)
        )
        Text(
            text = "Insert your password",
            fontSize = 10.sp,
        )
        TextField(
            value= password.value,
            label= {Text("Password")},
            onValueChange = {password.value = it}
        )
        Spacer(
            modifier= Modifier.height(10.dp)
        )

        errorMessage.value?.let {
            Text(text = it)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp))
        {
            Button(
                onClick = {
                    registerWithEmail(
                        auth = auth,
                        email = email.value,
                        password = password.value,
                        onSuccess = {
                            navController.navigate("main") {
                                popUpTo("registo") { inclusive = true }
                            }
                        },
                        onError = { error ->
                            errorMessage.value = error
                        }
                    )
                }
            ) {
                Text("Sign in")
            }
        }

    }
}


@Composable
fun EcraMain(navController: NavController) {

    Column(
        Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Select one of the options",
            fontSize = 20.sp,
        )
        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Button(
            onClick = { navController.navigate("experiments") },
        ) {
            Text("Experiments")
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )
        Button(
            onClick = { navController.navigate("sensors") },
        ) {
            Text("Sensors")
        }
        Spacer(
            modifier = Modifier.height(10.dp)
        )
        Button(
            onClick = { navController.navigate("visualize") },
        ) {
            Text("Visualize Data")
        }
        Spacer(
            modifier = Modifier.height(10.dp)
        )
    }
}


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
fun EcraSensors(navController: NavController){}

@Composable
fun EcraVisualization(navController: NavController){}

// --- ViewModel ---
private const val TAG = "FirestoreExample"

// --- ViewModel ---
class ExperimentViewModel : ViewModel() {

    private val db = Firebase.firestore

    fun addExperiment(
        title: String,
        objective: String,
        date: String,
        tags: List<String>,
        photos: List<String>,
        createdByUid: String
    ) {
        val experiment = mapOf(
            "title" to title,
            "objective" to objective,
            "date" to date,
            "tags" to tags,
            "createdByUid" to createdByUid,
            "photos" to photos
        )

        db.collection("experiments")
            .add(experiment)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Experiment added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding experiment", e)
            }
    }
}
// --- Composable ---
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

@Composable
fun EcraManageExperiments(navController: NavController) {
}
