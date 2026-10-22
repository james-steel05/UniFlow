package com.example.uniflow.ui.screens

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.uniflow.MainActivity
import com.example.uniflow.ui.Routes
import com.microsoft.identity.client.AuthenticationCallback
import com.microsoft.identity.client.IAuthenticationResult
import com.microsoft.identity.client.exception.MsalException

// Login screen with Microsoft SSO button
@Composable
fun SignInScreen(navController: NavHostController) {
    val context = LocalContext.current
    val activity = context as? Activity

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "UniFlow", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "One place for all your academic info")
        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = {
                val app = MainActivity.mSingleAccountApp
                if (app != null && activity != null) {
                    val scopes = arrayOf("User.Read")

                    // Trigger the interactive Microsoft login flow
                    app.signIn(
                        activity,
                        null,
                        scopes,
                        object : AuthenticationCallback {
                            override fun onSuccess(authenticationResult: IAuthenticationResult) {
                                val account = authenticationResult.account
                                val username = account.username

                                // Fixed: using getClaims() to retrieve the map of token claims
                                val name = account.getClaims()?.get("name")?.toString() ?: username

                                Log.d("MSAL", "Successfully signed in as: $name")

                                // Navigate to home and clear the login screen from the back stack
                                navController.navigate(Routes.HOME) {
                                    popUpTo(Routes.SIGN_IN) { inclusive = true }
                                }
                            }

                            override fun onError(exception: MsalException) {
                                Log.e("MSAL", "Authentication failed: ${exception.message}", exception)
                            }

                            override fun onCancel() {
                                Log.d("MSAL", "User cancelled the sign-in flow.")
                            }
                        }
                    )
                } else {
                    Log.w("MSAL", "MSAL app instance is not initialized yet.")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign in with Microsoft")
        }
    }
}