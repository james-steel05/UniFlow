package com.example.uniflow.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "module_content")
data class ModuleContentEntity(
    @PrimaryKey val moduleCode: String,
    val htmlContent: String,
    val lastUpdated: Long = System.currentTimeMillis()
)