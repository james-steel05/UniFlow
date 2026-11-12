package com.example.uniflow.data.repository

import com.example.uniflow.data.local.ModuleDao
import com.example.uniflow.data.local.ModuleEntity
import com.example.uniflow.data.local.TimetableDao
import com.example.uniflow.data.local.TimetableEntity
import com.example.uniflow.data.remote.LmsRemoteDataSource
import kotlinx.coroutines.flow.Flow

class ModuleRepository(
    private val moduleDao: ModuleDao,
    private val timetableDao: TimetableDao,
    private val remoteDataSource: LmsRemoteDataSource = LmsRemoteDataSource()
) {

    val allModules: Flow<List<ModuleEntity>> = moduleDao.getAllModules()
    val allClasses: Flow<List<TimetableEntity>> = timetableDao.getAllClasses()

    suspend fun syncModulesFromLms(userEmail: String, idToken: String? = null) {
        try {
            val remoteModules = remoteDataSource.fetchUserModules(userEmail, idToken)
            if (remoteModules.isNotEmpty()) {
                for (module in remoteModules) {
                    val existing = moduleDao.getModuleByCode(module.moduleCode)
                    if (existing != null) {
                        moduleDao.insertModule(module.copy(isFavourite = existing.isFavourite))
                    } else {
                        moduleDao.insertModule(module)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun updateReminder(id: Int, minutes: Int) = timetableDao.updateReminder(id, minutes)

    suspend fun insertAll(modules: List<ModuleEntity>) {
        moduleDao.insertModules(modules)
    }

    suspend fun toggleFavourite(code: String, isFavourite: Boolean) = moduleDao.setFavourite(code, isFavourite)
}