package com.example.uniflow.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.uniflow.MainActivity
import com.example.uniflow.ui.screens.*

// Wires every screen into the navigation graph with a dynamic start destination
@Composable
fun AppNavHost(navController: NavHostController, startDestination: String) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable(Routes.SIGN_IN) {
            SignInScreen(navController)
        }

        composable(Routes.TIMETABLE) {
            TimetableScreen(navController)
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                navController = navController,
            )
        }

        composable(Routes.MODULES) {
            ModulesScreen(navController)
        }

        composable(
            route = "${Routes.MODULE_DETAIL}/{moduleCode}",
            arguments = listOf(navArgument("moduleCode") { type = NavType.StringType })
        ) { backStackEntry ->
            val moduleCode = backStackEntry.arguments?.getString("moduleCode") ?: "Unknown"
            ModuleDetailScreen(navController, moduleCode)
        }

        composable(
            route = "${Routes.MODULE_DETAIL}/{moduleCode}",
            arguments = listOf(navArgument("moduleCode") { type = NavType.StringType })
        ) { backStackEntry ->
            val moduleCode = backStackEntry.arguments?.getString("moduleCode") ?: "Unknown"
            ModuleDetailScreen(navController, moduleCode)
        }
    }
}