package com.example.tp.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
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
        composable("add_experiments") { EcraAddExperiments(navController) }

        composable(
            route = "sensors/{experimentId}",
            arguments = listOf(navArgument("experimentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val experimentId =
                backStackEntry.arguments?.getString("experimentId") ?: return@composable
            EcraSensors(navController, experimentId)
        }

        composable(
            route = "visualize/{experimentId}",
            arguments = listOf(navArgument("experimentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val experimentId =
                backStackEntry.arguments?.getString("experimentId") ?: return@composable
            EcraVisualization(navController, experimentId)
        }
    }
}



