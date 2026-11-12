package com.example.uniflow.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TimetableDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClasses(classes: List<TimetableEntity>)

    @Query("SELECT * FROM timetable ORDER BY dayOfWeek ASC, startTime ASC")
    fun getAllClasses(): Flow<List<TimetableEntity>>

    @Query("UPDATE timetable SET reminderMinutes = :minutes WHERE id = :id")
    suspend fun updateReminder(id: Int, minutes: Int)

    @Query("DELETE FROM timetable")
    suspend fun deleteAllClasses()
}