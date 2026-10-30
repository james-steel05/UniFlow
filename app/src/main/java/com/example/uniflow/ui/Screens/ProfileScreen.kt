package com.example.uniflow.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.uniflow.MainActivity
import com.example.uniflow.ui.Routes
import com.example.uniflow.ui.components.BottomBar
import com.microsoft.identity.client.ISingleAccountPublicClientApplication
import com.microsoft.identity.client.exception.MsalException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    accessToken: String?, // Pass the token from MainActivity
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Trigger the fetch ONLY when the screen opens and a token exists
    LaunchedEffect(accessToken) {
        if (accessToken != null) {
            viewModel.fetchUserProfile(accessToken)
        } else {
            // Optional: Handle the case where there is no token
        }
    }

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
            when (val state = uiState) {
                is ProfileState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is ProfileState.Error -> {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }
                is ProfileState.Success -> {
                    Text(
                        text = state.profile.displayName ?: "Unknown Name",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(text = state.profile.mail ?: state.profile.userPrincipalName ?: "No email found")
                }
            }

            HorizontalDivider()

            Text("Preferences", style = MaterialTheme.typography.titleMedium)
            Text("Theme: Light")
            Text("Notifications: Enabled")

            Spacer(modifier = Modifier.weight(1f))

            // UPDATED: Actual MSAL Sign Out Logic
            Button(
                onClick = {
                    val app = MainActivity.mSingleAccountApp
                    if (app != null) {
                        app.signOut(object : ISingleAccountPublicClientApplication.SignOutCallback {
                            override fun onSignOut() {
                                // Clear the global token
                                MainActivity.currentAccessToken = null

                                // Navigate back to Sign In and clear the backstack
                                navController.navigate(Routes.SIGN_IN) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }

                            override fun onError(exception: MsalException) {
                                Log.e("MSAL_DEBUG", "Sign out failed", exception)
                            }
                        })
                    } else {
                        // Fallback just in case MSAL isn't initialized
                        navController.navigate(Routes.SIGN_IN) {
                            popUpTo(0) { inclusive = true }
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