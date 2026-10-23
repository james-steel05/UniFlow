package com.example.uniflow.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ModuleDao {

    // Create: Insert a new module. If it exists, overwrite it.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModule(module: ModuleEntity)

    // Create (Bulk): Insert a list of modules
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModules(modules: List<ModuleEntity>)

    // Smart sync that updates remote module details while protecting local favorite statuses
    @Transaction
    suspend fun insertOrUpdateModulesPreservingFavorites(modules: List<ModuleEntity>) {
        for (module in modules) {
            val existing = getModuleByCode(module.moduleCode)
            if (existing != null) {
                // Preserve the user's existing local favorite state
                insertModule(module.copy(isFavourite = existing.isFavourite))
            } else {
                insertModule(module)
            }
        }
    }

    // Read: Get all modules. Flow automatically emits new data when the DB changes.
    @Query("SELECT * FROM modules ORDER BY moduleCode ASC")
    fun getAllModules(): Flow<List<ModuleEntity>>

    // Read: Get a single module by its code
    @Query("SELECT * FROM modules WHERE moduleCode = :code")
    suspend fun getModuleByCode(code: String): ModuleEntity?

    // Update: Update an existing module
    @Update
    suspend fun updateModule(module: ModuleEntity)

    // Update: Toggle favourite status directly
    @Query("UPDATE modules SET isFavourite = :isFav WHERE moduleCode = :code")
    suspend fun setFavourite(code: String, isFav: Boolean)

    // Delete: Remove a specific module
    @Delete
    suspend fun deleteModule(module: ModuleEntity)

    // Delete: Clear all modules
    @Query("DELETE FROM modules")
    suspend fun deleteAllModules()
}