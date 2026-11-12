package com.example.uniflow.ui.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.uniflow.MainActivity
import com.example.uniflow.data.local.SettingsManager
import com.example.uniflow.ui.Routes
import com.example.uniflow.ui.components.BottomBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Collect the StateFlows from MainActivity
    val userNameState by MainActivity.currentUserName.collectAsState()
    val userEmailState by MainActivity.currentUserEmail.collectAsState()

    // Use them with fallbacks
    val displayName = userNameState ?: "Loading..."
    val displayEmail = userEmailState ?: "Loading..."
    val studentNumber = if (displayEmail != "Loading...") "EDUV" + displayEmail.hashCode().toString().takeLast(7) else "N/A"

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
            Text(displayName, style = MaterialTheme.typography.titleLarge)
            Text("Student Number: $studentNumber", style = MaterialTheme.typography.bodyMedium)
            Text(displayEmail, style = MaterialTheme.typography.bodyMedium)

            HorizontalDivider()

            Text("Preferences", style = MaterialTheme.typography.titleMedium)

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

            Button(
                onClick = {
                    scope.launch {
                        try {
                            // Clear variables
                            MainActivity.updateUserData(null, null)
                            MainActivity.currentAccessToken = null

                            // Run signOut on background thread to prevent crash
                            withContext(Dispatchers.IO) {
                                MainActivity.mSingleAccountApp?.signOut()
                            }

                            // Navigate back to Sign In
                            withContext(Dispatchers.Main) {
                                navController.navigate(Routes.SIGN_IN) {
                                    popUpTo(Routes.SIGN_IN) { inclusive = true }
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign Out")
            }
        }
    }
}