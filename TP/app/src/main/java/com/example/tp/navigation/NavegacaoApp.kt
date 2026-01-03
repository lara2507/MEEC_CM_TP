package com.example.tp.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tp.ui.screens.*


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavegacaoApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") { EcraLogin(navController) }
        composable("registo") { EcraCreateAccount(navController) }
        composable("main") { EcraMain(navController) }
        composable("experiments") { EcraExperiments(navController) }

        // New route: sensors, takes experimentId as argument
        composable(
            route = "sensors/{experimentId}",
            arguments = listOf(navArgument("experimentId") { defaultValue = "defaultExp" })
        ) { backStackEntry ->
            val experimentId = backStackEntry.arguments?.getString("experimentId") ?: "defaultExp"
            EcraSensors(navController, experimentId)
        }

//        composable("visualize") { EcraVisualization(navController) }
        composable("add_experiments") { EcraAddExperiments(navController) }
        composable("manage_experiments") { EcraManageExperiments(navController) }
    }
}

