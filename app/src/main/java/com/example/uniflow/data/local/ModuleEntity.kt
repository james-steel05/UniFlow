package com.example.uniflow.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "modules")
data class ModuleEntity(
    @PrimaryKey val moduleCode: String,
    val moduleName: String,
    val lecturerName: String,
    val isFavourite: Boolean = false
)