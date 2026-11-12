package com.example.uniflow.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.uniflow.data.local.AppDatabase
import com.example.uniflow.data.local.TimetableEntity
import com.example.uniflow.data.repository.ModuleRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TimetableViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ModuleRepository
    val classes: StateFlow<List<TimetableEntity>>

    init {
        val db = AppDatabase.getDatabase(application)
        repository = ModuleRepository(db.moduleDao(), db.timetableDao())

        classes = repository.allClasses.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun setReminder(classId: Int, minutes: Int) {
        viewModelScope.launch {
            repository.updateReminder(classId, minutes)
        }
    }
}