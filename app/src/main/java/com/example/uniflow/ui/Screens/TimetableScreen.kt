package com.example.uniflow.ui.screens

import androidx.compose.foundation.clickable
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

// Data class for our mock data
data class TimetableSlot(
    val moduleCode: String,
    val moduleName: String,
    val time: String,
    val venue: String,
    val day: String
)

@Composable
fun TimetableScreen(navController: NavHostController) {
    // Hardcoded data for testing
    val mockTimetable = listOf(
        TimetableSlot("ITMDA3-34", "Mobile Development", "08:00 - 10:00", "Room A1", "Monday"),
        TimetableSlot("SAD3-22", "Systems Analysis", "10:00 - 12:00", "Room B2", "Monday"),
        TimetableSlot("DBD3-12", "Database Design", "13:00 - 15:00", "Lab 3", "Tuesday")
    )

    Scaffold(
        topBar = { TopAppBar(title = { Text("Timetable") }) },
        bottomBar = { BottomBar(navController, Routes.TIMETABLE) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(mockTimetable) { slot ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            // UPDATED: Navigate passing the module code
                            navController.navigate("module_detail/${slot.moduleCode}")
                        }
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("${slot.day} | ${slot.time}", style = MaterialTheme.typography.labelMedium)
                        Text(slot.moduleName, style = MaterialTheme.typography.titleMedium)
                        Text("${slot.moduleCode} - ${slot.venue}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}