package com.example.uniflow.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ModuleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModule(module: ModuleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModules(modules: List<ModuleEntity>)

    // Sorts favourites to the top, then sorts alphabetically
    @Query("SELECT * FROM modules ORDER BY isFavourite DESC, moduleName ASC")
    fun getAllModules(): Flow<List<ModuleEntity>>

    @Query("SELECT * FROM modules WHERE moduleCode = :code")
    suspend fun getModuleByCode(code: String): ModuleEntity?

    @Update
    suspend fun updateModule(module: ModuleEntity)

    @Query("UPDATE modules SET isFavourite = :isFav WHERE moduleCode = :code")
    suspend fun setFavourite(code: String, isFav: Boolean)

    @Delete
    suspend fun deleteModule(module: ModuleEntity)

    @Query("DELETE FROM modules")
    suspend fun deleteAllModules()
}