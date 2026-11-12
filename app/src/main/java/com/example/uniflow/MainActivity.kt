package com.example.uniflow

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.uniflow.data.local.AppDatabase
import com.example.uniflow.data.local.SettingsManager
import com.example.uniflow.data.repository.ModuleRepository
import com.example.uniflow.ui.AppNavHost
import com.example.uniflow.ui.Routes
import com.microsoft.identity.client.*
import com.microsoft.identity.client.exception.MsalException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    companion object {
        var mSingleAccountApp: ISingleAccountPublicClientApplication? = null
            private set
        var currentAccessToken: String? = null

        // FIXED: Use StateFlow to guarantee UI updates
        private val _currentUserName = MutableStateFlow<String?>(null)
        val currentUserName: StateFlow<String?> = _currentUserName

        private val _currentUserEmail = MutableStateFlow<String?>(null)
        val currentUserEmail: StateFlow<String?> = _currentUserEmail

        // Helper to update both at once
        fun updateUserData(name: String?, email: String?) {
            _currentUserName.value = name
            _currentUserEmail.value = email
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MSAL_DEBUG", "MainActivity.onCreate started")

        SettingsManager.init(this)

        var startDestination by mutableStateOf<String?>(null)

        PublicClientApplication.createSingleAccountPublicClientApplication(
            this,
            R.raw.auth_config,
            object : IPublicClientApplication.ISingleAccountApplicationCreatedListener {
                override fun onCreated(application: ISingleAccountPublicClientApplication) {
                    mSingleAccountApp = application
                    Log.d("MSAL_DEBUG", "MSAL onCreated: Initialization successful. Instance assigned.")

                    application.getCurrentAccountAsync(object : ISingleAccountPublicClientApplication.CurrentAccountCallback {
                        override fun onAccountLoaded(activeAccount: IAccount?) {
                            if (activeAccount != null) {
                                val username = activeAccount.username

                                // Update the StateFlow immediately
                                updateUserData(
                                    name = username.substringBefore("@").replaceFirstChar { it.uppercase() },
                                    email = username
                                )

                                Log.d("MSAL_DEBUG", "Found active user session: $username")

                                val silentParams = AcquireTokenSilentParameters.Builder()
                                    .withScopes(listOf("User.Read"))
                                    .forAccount(activeAccount)
                                    .fromAuthority(activeAccount.authority)
                                    .withCallback(object : SilentAuthenticationCallback {
                                        override fun onSuccess(authenticationResult: IAuthenticationResult) {
                                            val accessToken = authenticationResult.accessToken
                                            currentAccessToken = accessToken
                                            startDestination = Routes.TIMETABLE

                                            CoroutineScope(Dispatchers.IO).launch {
                                                try {
                                                    val database = AppDatabase.getDatabase(applicationContext)
                                                    val repository = ModuleRepository(database.moduleDao(), database.timetableDao())
                                                    repository.syncModulesFromLms(username, accessToken)
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                }
                                            }
                                        }

                                        override fun onError(exception: MsalException) {
                                            Log.e("MSAL_DEBUG", "Silent token acquisition failed", exception)
                                            startDestination = Routes.TIMETABLE

                                            CoroutineScope(Dispatchers.IO).launch {
                                                try {
                                                    val database = AppDatabase.getDatabase(applicationContext)
                                                    val repository = ModuleRepository(database.moduleDao(), database.timetableDao())
                                                    repository.syncModulesFromLms(username, null)
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                }
                                            }
                                        }
                                    })
                                    .build()

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
            val isDarkMode by SettingsManager.isDarkMode.collectAsState()

            MaterialTheme(
                colorScheme = if (isDarkMode) darkColorScheme() else lightColorScheme()
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val destination = startDestination
                    if (destination != null) {
                        val navController = rememberNavController()
                        AppNavHost(navController = navController, startDestination = destination)
                    }
                }
            }
        }
    }
}