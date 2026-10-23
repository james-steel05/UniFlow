package com.example.uniflow.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// Represents a single module in the local SQLite database
@Entity(tableName = "modules")
data class ModuleEntity(
    @PrimaryKey val moduleCode: String,
    val moduleName: String,
    val lecturerName: String,
    val isFavourite: Boolean = false,
    val lastSyncedAt: Long = System.currentTimeMillis()
)
