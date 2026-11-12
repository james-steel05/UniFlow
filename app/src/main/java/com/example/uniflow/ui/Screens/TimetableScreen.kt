package com.example.uniflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.uniflow.ui.Routes
import com.example.uniflow.ui.components.BottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableScreen(
    navController: NavHostController,
    viewModel: TimetableViewModel = viewModel()
) {
    val classes by viewModel.classes.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Timetable") }) },
        bottomBar = { BottomBar(navController, Routes.TIMETABLE) }
    ) { padding ->
        if (classes.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(classes) { slot ->
                    TimetableCard(
                        slot = slot,
                        onReminderChanged = { newMinutes ->
                            viewModel.setReminder(slot.id, newMinutes)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableCard(
    slot: com.example.uniflow.data.local.TimetableEntity,
    onReminderChanged: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("${slot.dayOfWeek} | ${slot.startTime} - ${slot.endTime}", style = MaterialTheme.typography.labelMedium)
            Text(slot.moduleName, style = MaterialTheme.typography.titleMedium)
            Text("${slot.moduleCode} - ${slot.venue}", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))

            // Reminder Dropdown
            Box {
                OutlinedButton(onClick = { expanded = true }) {
                    Text("Reminder: ${slot.reminderMinutes} mins before")
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(
                        text = { Text("1 Hour (60 min)") },
                        onClick = { onReminderChanged(60); expanded = false }
                    )
                    DropdownMenuItem(
                        text = { Text("30 Minutes") },
                        onClick = { onReminderChanged(30); expanded = false }
                    )
                    DropdownMenuItem(
                        text = { Text("10 Minutes") },
                        onClick = { onReminderChanged(10); expanded = false }
                    )
                }
            }
        }
    }
}