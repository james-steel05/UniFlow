package com.example.uniflow.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import com.example.uniflow.ui.Routes

// Data class for one bottom bar item
data class BottomItem(val label: String, val icon: ImageVector, val route: String)

// Bottom navigation used across main screens
@Composable
fun BottomBar(navController: NavHostController, currentRoute: String?) {
    val items = listOf(
        BottomItem("Home", Icons.Filled.Home, Routes.HOME),
        BottomItem("Timetable", Icons.Filled.DateRange, Routes.TIMETABLE),
        BottomItem("Modules", Icons.Filled.Menu, Routes.MODULES),
        BottomItem("Messages", Icons.Filled.Email, Routes.MESSAGES),
        BottomItem("Profile", Icons.Filled.Person, Routes.PROFILE)
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    // Avoid stacking the same screen twice
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Routes.HOME) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}