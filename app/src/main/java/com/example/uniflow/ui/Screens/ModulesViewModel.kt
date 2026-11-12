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
        repository = ModuleRepository(database.moduleDao(), database.timetableDao())

        modules = repository.allModules.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    }

    // This function must exist for the Star icon to work
    fun toggleFavourite(moduleCode: String, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.toggleFavourite(moduleCode, !currentStatus)
        }
    }
}