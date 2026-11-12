package com.example.uniflow.data.remote

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

class LmsRemoteDataSource {

    private val supabaseUrl = "https://nbwajoxxqvzoazjfpoac.supabase.co/rest/v1/lms_html_content"
    private val apiKey = "sb_publishable_YlIs-L-o8mphFyllUUZk_nA_khFFgut7"

    // Fetches the HTML text instead of a list of modules
    suspend fun fetchHtmlContent(userEmail: String, category: String, itemCode: String = "main"): String? = withContext(Dispatchers.IO) {
        try {
            // Filter by email, category, and item_code
            val url = URL("$supabaseUrl?user_email=eq.$userEmail&category=eq.$category&item_code=eq.$itemCode")
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("apikey", apiKey)
                setRequestProperty("Authorization", "Bearer $apiKey")
                connectTimeout = 10000
                readTimeout = 10000
            }

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val jsonResponse = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonArray = JSONArray(jsonResponse)

                if (jsonArray.length() > 0) {
                    // Return the html_content string from the first matching row
                    return@withContext jsonArray.getJSONObject(0).getString("html_content")
                }
            }
        } catch (e: Exception) {
            Log.e("LMS_DEBUG", "Failed to fetch HTML", e)
        }
        return@withContext null
    }
}