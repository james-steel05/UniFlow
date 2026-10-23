package com.example.uniflow.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.uniflow.MainActivity
import com.example.uniflow.ui.Routes
import com.example.uniflow.ui.components.BottomBar
import com.microsoft.identity.client.ISingleAccountPublicClientApplication
import com.microsoft.identity.client.exception.MsalException

// User profile and settings
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController) {
    var isSigningOut by remember { mutableStateOf(false) }

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
                onClick = {
                    val app = MainActivity.mSingleAccountApp
                    if (app != null) {
                        isSigningOut = true
                        app.signOut(object : ISingleAccountPublicClientApplication.SignOutCallback {
                            override fun onSignOut() {
                                Log.d("MSAL_DEBUG", "Successfully signed out from MSAL session.")
                                navController.navigate(Routes.SIGN_IN) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }

                            override fun onError(exception: MsalException) {
                                Log.e("MSAL_DEBUG", "Error during sign out", exception)
                                isSigningOut = false
                            }
                        })
                    } else {
                        // Fallback if MSAL instance is null
                        navController.navigate(Routes.SIGN_IN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                },
                enabled = !isSigningOut,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isSigningOut) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Sign Out")
                }
            }
        }
    }
}