package com.example.uniflow.data.api

// Represents a single class slot from the timetable
data class ScrapedClass(
    val moduleCode: String,
    val moduleName: String,
    val time: String,
    val venue: String
)