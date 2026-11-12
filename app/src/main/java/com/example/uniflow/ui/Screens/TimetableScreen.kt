package com.example.uniflow.ui.Screens

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.uniflow.data.local.AppDatabase
import com.example.uniflow.data.local.TimetableEntity
import com.example.uniflow.data.repository.ModuleRepository
import com.example.uniflow.ui.Routes
import com.example.uniflow.ui.components.BottomBar
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// --- 1. The ViewModel (Merged here so Android Studio can't lose it) ---
class TimetableViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ModuleRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = ModuleRepository(db.moduleDao(), db.timetableDao())
    }

    val classes: StateFlow<List<TimetableEntity>> = repository.allClasses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isSyncing: StateFlow<Boolean> = kotlinx.coroutines.flow.MutableStateFlow(false)

    fun setReminder(id: Int, minutes: Int) {
        viewModelScope.launch {
            repository.updateReminder(id, minutes)
        }
    }
}

// --- 2. The Screen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableScreen(
    navController: NavHostController,
    viewModel: TimetableViewModel = viewModel()
) {
    val classes by viewModel.classes.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Timetable") }) },
        bottomBar = { BottomBar(navController, Routes.TIMETABLE) }
    ) { padding ->
        if (classes.isEmpty() && isSyncing) {
            // Show spinner while syncing
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (classes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No classes scheduled.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(classes, key = { it.id }) { slot ->
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
    slot: TimetableEntity,
    onReminderChanged: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("${slot.dayOfWeek} | ${slot.startTime} - ${slot.endTime}", style = MaterialTheme.typography.labelMedium)
            Text(slot.moduleCode, style = MaterialTheme.typography.titleMedium)
            Text(slot.venue, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))

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