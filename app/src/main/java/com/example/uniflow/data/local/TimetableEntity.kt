package com.example.uniflow.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "timetable")
data class TimetableEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val moduleCode: String,
    val moduleName: String,
    val dayOfWeek: String, // e.g., "Monday"
    val startTime: String, // e.g., "08:00"
    val endTime: String,   // e.g., "10:00"
    val venue: String,
    val reminderMinutes: Int = 30 // 60 for 1 hour, 30, or 10
)