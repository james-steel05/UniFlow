package com.example.uniflow.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import com.example.uniflow.ui.Routes

data class BottomItem(val label: String, val icon: ImageVector, val route: String)

@Composable
fun BottomBar(navController: NavHostController, currentRoute: String?) {
    // UPDATED: Only 4 items now
    val items = listOf(
        BottomItem("Timetable", Icons.Filled.DateRange, Routes.TIMETABLE),
        BottomItem("Modules", Icons.Filled.Menu, Routes.MODULES),
        BottomItem("Profile", Icons.Filled.Person, Routes.PROFILE)
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // UPDATED: Home is gone, so pop up to Timetable
                            popUpTo(Routes.TIMETABLE) { saveState = true }
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