package com.example.uniflow.data.remote

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class LmsRemoteDataSource {

    private val supabaseUrl = "https://nbwajoxxqvzoazjfpoac.supabase.co/rest/v1/lms_html_content"
    private val apiKey = "sb_publishable_YlIsL-o8mphFyllUUZk_nA_khFFgut7"

    suspend fun fetchHtmlContent(userEmail: String, category: String, itemCode: String = "main"): String? = withContext(Dispatchers.IO) {
        try {
            val encodedEmail = URLEncoder.encode(userEmail, "UTF-8")
            val encodedItemCode = URLEncoder.encode(itemCode, "UTF-8")

            // Added item_code=eq.$encodedItemCode to the URL
            val url = URL("$supabaseUrl?user_email=eq.$encodedEmail&category=eq.$category&item_code=eq.$encodedItemCode")

            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("apikey", apiKey)
                setRequestProperty("Accept", "application/json")
                connectTimeout = 10000
                readTimeout = 10000
            }

            val responseCode = connection.responseCode
            Log.d("LMS_DEBUG", "Fetching $category ($itemCode) -> Response Code: $responseCode")

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val jsonResponse = connection.inputStream.bufferedReader().use { it.readText() }

                // THIS IS THE NEW LOG: It will print the exact response from Supabase
                Log.d("LMS_DEBUG", "Raw Supabase Response: $jsonResponse")

                val jsonArray = JSONArray(jsonResponse)

                if (jsonArray.length() > 0) {
                    val html = jsonArray.getJSONObject(0).getString("html_content")
                    Log.d("LMS_DEBUG", "Successfully extracted HTML length: ${html.length}")
                    return@withContext html
                } else {
                    Log.e("LMS_DEBUG", "No data found for $category with itemCode $itemCode.")
                }
            } else {
                val errorStream = connection.errorStream?.bufferedReader()?.use { it.readText() }
                Log.e("LMS_DEBUG", "Server error for $category: $responseCode. Details: $errorStream")
            }
        } catch (e: Exception) {
            Log.e("LMS_DEBUG", "Exception fetching $category", e)
        }
        return@withContext null
    }
}