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

// Central feed of announcements
@Composable
fun AnnouncementsScreen(navController: NavHostController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Announcements") }) },
        bottomBar = { BottomBar(navController, Routes.ANNOUNCEMENTS) }
    ) { padding ->
        val announcements = listOf(
            "Campus closed on Friday",
            "New library hours",
            "Exam timetable released"
        )

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(announcements) { title ->
                ListItem(
                    headlineContent = { Text(title) },
                    supportingContent = { Text("Tap to read more") }
                )
                Divider()
            }
        }
    }
}