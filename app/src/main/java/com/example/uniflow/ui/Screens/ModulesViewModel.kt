package com.example.uniflow.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.uniflow.data.local.AppDatabase
import com.example.uniflow.data.local.ModuleEntity
import com.example.uniflow.data.repository.ModuleRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ModulesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ModuleRepository

    // UI state: Flow of modules from the database
    val modules: StateFlow<List<ModuleEntity>>

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = ModuleRepository(database.moduleDao())

        // Convert Flow to StateFlow for Compose
        modules = repository.allModules.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    private suspend fun syncData(email: String) {
        _isLoading.value = true
        try {
            repository.syncModulesFromLms(email)
        } finally {
            _isLoading.value = false
        }
    }

    // CRUD Functions for UI
    fun syncWithRemote(email: String) {
        viewModelScope.launch {
            syncData(email)
        }
    }

    fun toggleFavourite(moduleCode: String, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.toggleFavourite(moduleCode, !currentStatus)
        }
    }

    fun addModule(module: ModuleEntity) {
        viewModelScope.launch { repository.insert(module) }
    }

    fun removeModule(module: ModuleEntity) {
        viewModelScope.launch { repository.delete(module) }
    }
}