package com.example.uniflow.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "timetable")
data class TimetableEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // This fixes the 'id' error!
    val moduleCode: String,
    val moduleName: String,
    val dayOfWeek: String,
    val startTime: String,
    val endTime: String,
    val venue: String,
    val reminderMinutes: Int = 30
)