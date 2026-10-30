package com.example.uniflow.data.api

import retrofit2.http.GET
import retrofit2.http.Header

// Represents the data returned by Microsoft Graph
data class GraphUserProfile(
    val displayName: String?, // The user's full name
    val mail: String?,        // Their email
    val userPrincipalName: String? // Fallback email (student number format)
)

// The interface to make the network call

interface GraphApiService {
    @GET("v1.0/me")
    suspend fun getUserProfile(
        @Header("Authorization") bearerToken: String
    ): GraphUserProfile
}