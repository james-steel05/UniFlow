package com.example.uniflow.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.uniflow.ui.screens.*

// Wires every screen into the navigation graph with a dynamic start destination
@Composable
fun AppNavHost(navController: NavHostController, startDestination: String) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable(Routes.SIGN_IN) {
            SignInScreen(navController)
        }
        composable(Routes.HOME) {
            HomeScreen(navController)
        }
        composable(Routes.TIMETABLE) {
            TimetableScreen(navController)
        }
        composable(Routes.ANNOUNCEMENTS) {
            AnnouncementsScreen(navController)
        }
        composable(Routes.MESSAGES) {
            MessagesScreen(navController)
        }
        composable(Routes.PROFILE) {
            ProfileScreen(navController)
        }
        composable(Routes.MODULES) {
            ModulesScreen(navController)
        }
        composable(Routes.MODULE_DETAIL) {
            ModuleDetailScreen(navController)
        }
    }
}