package com.example.uniflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.uniflow.data.local.SettingsManager
import com.example.uniflow.ui.Routes
import com.example.uniflow.ui.components.BottomBar

@Composable
fun ProfileScreen(navController: NavHostController) {
    val context = LocalContext.current

    // Observe the settings states
    val isDarkMode by SettingsManager.isDarkMode.collectAsState()
    val notificationsEnabled by SettingsManager.notificationsEnabled.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Profile") }) },
        bottomBar = { BottomBar(navController, Routes.PROFILE) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User Info
            Text("John Doe", style = MaterialTheme.typography.titleLarge)
            Text("EDUV1234567", style = MaterialTheme.typography.bodyMedium)
            Text("john@vossie.net", style = MaterialTheme.typography.bodyMedium)

            HorizontalDivider()

            Text("Preferences", style = MaterialTheme.typography.titleMedium)

            // Dark Mode Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Dark Mode")
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = { SettingsManager.setDarkMode(context, it) }
                )
            }

            // Notifications Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Notifications")
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = { SettingsManager.setNotifications(context, it) }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Sign Out Button
            Button(
                onClick = {
                    // TODO: Add actual MSAL sign-out logic here
                    navController.navigate(Routes.SIGN_IN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign Out")
            }
        }
    }
}