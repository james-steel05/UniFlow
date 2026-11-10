package com.example.uniflow.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.uniflow.MainActivity
import com.example.uniflow.ui.screens.*

// Wires every screen into the navigation graph with a dynamic start destination
@Composable
fun AppNavHost(navController: NavHostController, startDestination: String) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable(Routes.SIGN_IN) {
            SignInScreen(navController)
        }

        // Home and Messages have been removed

        composable(Routes.TIMETABLE) {
            TimetableScreen(navController)
        }
        composable(Routes.PROFILE) {
            ProfileScreen(
                navController = navController,
                accessToken = MainActivity.currentAccessToken // Pass the token here
            )
        }

        composable(Routes.MODULES) {
            ModulesScreen(navController)
        }

        // Updated to extract the moduleCode from the navigation route
        composable(Routes.MODULE_DETAIL) { backStackEntry ->
            val moduleCode = backStackEntry.arguments?.getString("moduleCode") ?: "Unknown"
            ModuleDetailScreen(navController, moduleCode)
        }
    }
}