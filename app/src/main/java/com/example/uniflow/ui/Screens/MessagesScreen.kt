package com.example.uniflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.uniflow.ui.Routes
import com.example.uniflow.ui.components.BottomBar

// List of Teams conversations
@Composable
fun MessagesScreen(navController: NavHostController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Messages") }) },
        bottomBar = { BottomBar(navController, Routes.MESSAGES) }
    ) { padding ->
        val chats = listOf(
            "Example 1",
            "Example 2",
            "Example 3"
        )

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chats) { name ->
                ListItem(
                    headlineContent = { Text(name) },
                    supportingContent = { Text("Tap to open chat") }
                )
                Divider()
            }
        }
    }
}