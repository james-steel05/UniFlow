package com.example.uniflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.uniflow.ui.Routes
import com.example.uniflow.ui.components.BottomBar

// User profile and settings
@Composable
fun ProfileScreen(navController: NavHostController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Profile") }) },
        bottomBar = { BottomBar(navController, Routes.PROFILE) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("John Doe", style = MaterialTheme.typography.titleLarge)
            Text("EDUV1234567")
            Text("john@vossie.net")

            Divider()

            Text("Preferences", style = MaterialTheme.typography.titleMedium)
            Text("Theme: Light")
            Text("Notifications: Enabled")

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { navController.navigate(Routes.SIGN_IN) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign Out")
            }
        }
    }
}