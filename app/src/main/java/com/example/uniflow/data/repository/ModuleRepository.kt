package com.example.uniflow.data.repository

import com.example.uniflow.data.local.ModuleDao
import com.example.uniflow.data.local.ModuleEntity
import com.example.uniflow.data.remote.LmsRemoteDataSource
import kotlinx.coroutines.flow.Flow

class ModuleRepository(
    private val moduleDao: ModuleDao,
    private val remoteDataSource: LmsRemoteDataSource = LmsRemoteDataSource()
) {

    val allModules: Flow<List<ModuleEntity>> = moduleDao.getAllModules()

    suspend fun syncModulesFromLms(userEmail: String, idToken: String? = null) {
        try {
            val remoteModules = remoteDataSource.fetchUserModules(userEmail, idToken)
            if (remoteModules.isNotEmpty()) {
                moduleDao.insertOrUpdateModulesPreservingFavorites(remoteModules)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun insert(module: ModuleEntity) {
        moduleDao.insertModule(module)
    }

    suspend fun insertAll(modules: List<ModuleEntity>) {
        moduleDao.insertModules(modules)
    }

    suspend fun toggleFavourite(code: String, isFavourite: Boolean) {
        moduleDao.setFavourite(code, isFavourite)
    }

    suspend fun delete(module: ModuleEntity) {
        moduleDao.deleteModule(module)
    }
}