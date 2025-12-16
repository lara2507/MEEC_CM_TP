package com.example.tp

import android.os.Bundle
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
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tp.ui.theme.TPTheme
import com.google.firebase.auth.FirebaseAuth




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
            EcraMain()
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
fun EcraMain(){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp) // Margem lateral
            .padding(
                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            )
    ) {
        // Topo: Bem-vindo
        Text(
            text = "Bem-vindo à App",
            fontSize = 28.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Meio: Conteúdo central
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Aqui vai o conteúdo principal da tela",
                fontSize = 18.sp
            )
        }

        // Rodapé: Botões
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { /* ação do botão 1 */ }) {
                Text("Botão 1")
            }
            Button(onClick = { /* ação do botão 2 */ }) {
                Text("Botão 2")
            }
            Button(onClick = { /* ação do botão 3 */ }) {
                Text("Botão 3")
            }
        }
    }
}
