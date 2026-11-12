package com.example.uniflow.data.local

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// Simple local storage for user preferences
object SettingsManager {
    private const val PREFS_NAME = "uniflow_prefs"
    private const val KEY_DARK_MODE = "dark_mode"
    private const val KEY_NOTIFICATIONS = "notifications"

    // State flows to update the UI instantly
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode

    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled

    // Initialize from SharedPreferences
    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _isDarkMode.value = prefs.getBoolean(KEY_DARK_MODE, false)
        _notificationsEnabled.value = prefs.getBoolean(KEY_NOTIFICATIONS, true)
    }

    fun setDarkMode(context: Context, enabled: Boolean) {
        _isDarkMode.value = enabled
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
    }

    fun setNotifications(context: Context, enabled: Boolean) {
        _notificationsEnabled.value = enabled
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_NOTIFICATIONS, enabled).apply()
    }
}