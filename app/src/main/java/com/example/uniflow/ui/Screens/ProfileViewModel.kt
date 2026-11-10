package com.example.uniflow.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uniflow.data.api.GraphApiService
import com.example.uniflow.data.api.GraphUserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val profile: GraphUserProfile) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val uiState: StateFlow<ProfileState> = _uiState

    private val apiService: GraphApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://graph.microsoft.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GraphApiService::class.java)
    }

    fun fetchUserProfile(accessToken: String) {
        viewModelScope.launch {
            _uiState.value = ProfileState.Loading
            try {
                // Ensure the token has the Bearer prefix
                val tokenHeader = if (accessToken.startsWith("Bearer ")) accessToken else "Bearer $accessToken"
                val profile = apiService.getUserProfile(tokenHeader)
                _uiState.value = ProfileState.Success(profile)
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error fetching profile", e)
                _uiState.value = ProfileState.Error("Failed to load: ${e.message}")
            }
        }
    }
}