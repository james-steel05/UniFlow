package com.example.uniflow.ui.Screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.uniflow.data.local.AppDatabase
import com.example.uniflow.data.repository.ModuleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ContentState {
    object Loading : ContentState()
    data class Success(val html: String) : ContentState()
    data class Error(val message: String) : ContentState()
}

class ModuleDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ModuleRepository

    private val _contentState = MutableStateFlow<ContentState>(ContentState.Loading)
    val contentState: StateFlow<ContentState> = _contentState

    init {
        val db = AppDatabase.getDatabase(application)
        repository = ModuleRepository(db.moduleDao(), db.timetableDao())
    }

    // Notice the forceRefresh parameter here
    fun loadContent(moduleCode: String, userEmail: String?, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _contentState.value = ContentState.Loading

            // 1. Check local DB first (unless we are forcing a refresh)
            var html = if (forceRefresh) null else repository.getModuleContent(moduleCode)

            // 2. If not in DB, fetch from Supabase
            if (html == null && userEmail != null) {
                repository.syncModuleContent(moduleCode, userEmail)
                html = repository.getModuleContent(moduleCode) // Try getting it again
            }

            // 3. Update UI State
            if (html != null) {
                _contentState.value = ContentState.Success(html)
            } else {
                _contentState.value = ContentState.Error("Could not download content. Check internet or module code.")
            }
        }
    }
}