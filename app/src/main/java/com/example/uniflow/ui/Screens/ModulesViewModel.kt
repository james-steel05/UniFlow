package com.example.uniflow.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.uniflow.data.local.AppDatabase
import com.example.uniflow.data.local.ModuleEntity
import com.example.uniflow.data.repository.ModuleRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ModulesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ModuleRepository

    // These must match the names used in ModulesScreen!
    val modules: StateFlow<List<ModuleEntity>>
    val isLoading = kotlinx.coroutines.flow.MutableStateFlow(false)

    init {
        val database = AppDatabase.getDatabase(application)
        repository = ModuleRepository(database.moduleDao())

        modules = repository.allModules.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Dummy data for testing if DB is empty
        viewModelScope.launch {
            if (repository.allModules.first().isEmpty()) {
                repository.insertAll(listOf(
                    ModuleEntity("ITMDA3-34", "Mobile Development", "Mr Tagutanazvo"),
                    ModuleEntity("SAD3-22", "Systems Analysis", "Mrs Smith"),
                    ModuleEntity("DBD3-12", "Database Design", "Mr Jones")
                ))
            }
        }
    }

    // This function must exist for the Star icon to work
    fun toggleFavourite(moduleCode: String, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.toggleFavourite(moduleCode, !currentStatus)
        }
    }
}