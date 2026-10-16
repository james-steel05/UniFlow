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

// Weekly timetable list
@Composable
fun TimetableScreen(navController: NavHostController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Timetable") }) },
        bottomBar = { BottomBar(navController, Routes.TIMETABLE) }
    ) { padding ->
        val classes = listOf(
            "Module Example 1 - 08:00 - Room A1",
            "Module Example 2 - 10:00 - Room B2",
            "Module Example 3 - 13:00 - Room C3"
        )

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(classes) { item ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = item,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}