package com.example.uniflow.ui.Screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.uniflow.MainActivity
import com.example.uniflow.ui.Routes
import com.microsoft.identity.client.AuthenticationCallback
import com.microsoft.identity.client.IAuthenticationResult
import com.microsoft.identity.client.SignInParameters
import com.microsoft.identity.client.exception.MsalException

@Composable
fun SignInScreen(navController: NavHostController) {
    var isLoggingIn by remember { mutableStateOf(false) }
    // Get the current Activity context required by MSAL
    val activity = LocalContext.current as android.app.Activity

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
                isLoggingIn = true
                val app = MainActivity.mSingleAccountApp

                if (app == null) {
                    Log.e("MSAL_DEBUG", "MSAL app not initialized yet")
                    isLoggingIn = false
                    return@Button
                }

                // Use SignInParameters.builder() to avoid compiler errors
                val params = SignInParameters.builder()
                    .withActivity(activity)
                    .withScopes(listOf("User.Read"))
                    .withCallback(object : AuthenticationCallback {
                        override fun onSuccess(authenticationResult: IAuthenticationResult?) {
                            authenticationResult?.let { result ->
                                // Save the token globally so ProfileScreen can use it
                                MainActivity.currentAccessToken = result.accessToken

                                // Navigate to Timetable and clear the backstack
                                navController.navigate(Routes.TIMETABLE) {
                                    popUpTo(Routes.SIGN_IN) { inclusive = true }
                                }
                            }
                            isLoggingIn = false
                        }

                        override fun onError(exception: MsalException?) {
                            Log.e("MSAL_DEBUG", "Login failed", exception)
                            isLoggingIn = false
                        }

                        override fun onCancel() {
                            Log.d("MSAL_DEBUG", "User cancelled login")
                            isLoggingIn = false
                        }
                    })
                    .build()

                // Trigger the interactive Microsoft login popup
                app.signIn(params)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoggingIn
        ) {
            if (isLoggingIn) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Sign in with Microsoft")
            }
        }
    }
}