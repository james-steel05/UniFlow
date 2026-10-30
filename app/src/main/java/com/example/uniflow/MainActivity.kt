package com.example.uniflow

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.uniflow.data.local.AppDatabase
import com.example.uniflow.data.repository.ModuleRepository
import com.example.uniflow.ui.AppNavHost
import com.example.uniflow.ui.Routes
import com.microsoft.identity.client.*
import com.microsoft.identity.client.exception.MsalException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    companion object {
        var mSingleAccountApp: ISingleAccountPublicClientApplication? = null
            private set
        var currentAccessToken: String? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MSAL_DEBUG", "MainActivity.onCreate started")

        var startDestination by mutableStateOf<String?>(null)

        // Initialize MSAL asynchronously on startup
        PublicClientApplication.createSingleAccountPublicClientApplication(
            this,
            R.raw.auth_config,
            object : IPublicClientApplication.ISingleAccountApplicationCreatedListener {
                override fun onCreated(application: ISingleAccountPublicClientApplication) {
                    mSingleAccountApp = application
                    Log.d("MSAL_DEBUG", "MSAL onCreated: Initialization successful. Instance assigned.")

                    // Check if an account is already signed in
                    application.getCurrentAccountAsync(object : ISingleAccountPublicClientApplication.CurrentAccountCallback {
                        override fun onAccountLoaded(activeAccount: IAccount?) {
                            if (activeAccount != null) {
                                val username = activeAccount.username
                                Log.d("MSAL_DEBUG", "Found active user session: $username")

                                val silentParams = AcquireTokenSilentParameters.Builder()
                                    .withScopes(listOf("User.Read"))
                                    .forAccount(activeAccount)
                                    .fromAuthority(activeAccount.authority)
                                    .withCallback(object : SilentAuthenticationCallback {
                                        override fun onSuccess(authenticationResult: IAuthenticationResult) {
                                            val accessToken = authenticationResult.accessToken

                                            // 2. Save the token globally
                                            currentAccessToken = accessToken

                                            // 3. Move the user to the Timetable screen
                                            startDestination = Routes.TIMETABLE

                                            CoroutineScope(Dispatchers.IO).launch {
                                                try {
                                                    val database = AppDatabase.getDatabase(applicationContext)
                                                    val repository = ModuleRepository(database.moduleDao())
                                                    repository.syncModulesFromLms(username, accessToken)
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                }
                                            }
                                        }

                                        override fun onError(exception: MsalException) {
                                            Log.e("MSAL_DEBUG", "Silent token acquisition failed", exception)

                                            // Even if the token fails, if they have an account, let them in
                                            startDestination = Routes.TIMETABLE

                                            CoroutineScope(Dispatchers.IO).launch {
                                                try {
                                                    val database = AppDatabase.getDatabase(applicationContext)
                                                    val repository = ModuleRepository(database.moduleDao())
                                                    repository.syncModulesFromLms(username, null)
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                }
                                            }
                                        }
                                    })
                                    .build()

                                // Execute the silent request
                                application.acquireTokenSilentAsync(silentParams)

                            } else {
                                Log.d("MSAL_DEBUG", "No active account. Showing Sign In.")
                                startDestination = Routes.SIGN_IN
                            }
                        }

                        override fun onAccountChanged(priorAccount: IAccount?, currentAccount: IAccount?) {}

                        override fun onError(exception: MsalException) {
                            Log.e("MSAL_DEBUG", "Failed to load current account", exception)
                            startDestination = Routes.SIGN_IN
                        }
                    })
                }

                override fun onError(exception: MsalException) {
                    Log.e("MSAL_DEBUG", "MSAL onError: Initialization failed: ${exception.message}", exception)
                    startDestination = Routes.SIGN_IN
                }
            }
        )

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val destination = startDestination
                    // 4. Only show the app once we know if they are logged in or not
                    if (destination != null) {
                        val navController = rememberNavController()
                        AppNavHost(navController = navController, startDestination = destination)
                    }
                }
            }
        }
    }
}