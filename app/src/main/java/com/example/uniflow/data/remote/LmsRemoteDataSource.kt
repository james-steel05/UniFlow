package com.example.uniflow.data.remote

import com.example.uniflow.data.local.ModuleEntity
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

class LmsRemoteDataSource {

    private val supabaseUrl = "https://nbwajoxxqvzoazjfpoac.supabase.co/rest/v1/modules"
    private val apiKey = "sb_publishable_YlIsL-o8mphFyllUUZk_nA_khFFgut7"

    suspend fun fetchUserModules(userEmail: String, idToken: String? = null): List<ModuleEntity> = withContext(Dispatchers.IO) {
        Log.d("LMS_DEBUG", "Fetching modules for email: $userEmail")
        val modulesList = mutableListOf<ModuleEntity>()
        try {
            val url = URL("$supabaseUrl?user_email=eq.$userEmail")
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("apikey", apiKey)
                setRequestProperty("Authorization", "Bearer $apiKey")

                if (!idToken.isNullOrEmpty()) {
                    setRequestProperty("X-MSAL-Token", idToken)
                }

                connectTimeout = 10000
                readTimeout = 10000
            }

            val responseCode = connection.responseCode
            Log.d("LMS_DEBUG", "Supabase Response Code: $responseCode")

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val jsonResponse = connection.inputStream.bufferedReader().use { it.readText() }
                Log.d("LMS_RESPONSE", "Raw JSON: $jsonResponse")

                val jsonArray = JSONArray(jsonResponse)
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    modulesList.add(
                        ModuleEntity(
                            moduleCode = obj.getString("module_code"),
                            moduleName = obj.getString("module_name"),
                            lecturerName = obj.getString("lecturer_name"),
                            isFavourite = false
                        )
                    )
                }
            } else {
                Log.d("LMS_DEBUG", "Server returned error status code: $responseCode")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext modulesList
    }
}