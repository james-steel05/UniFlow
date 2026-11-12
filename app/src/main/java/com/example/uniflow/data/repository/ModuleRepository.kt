package com.example.uniflow.data.repository

import com.example.uniflow.data.local.ModuleDao
import com.example.uniflow.data.local.ModuleEntity
import com.example.uniflow.data.local.TimetableDao
import com.example.uniflow.data.local.TimetableEntity
import com.example.uniflow.data.remote.LmsRemoteDataSource
import kotlinx.coroutines.flow.Flow
import org.jsoup.Jsoup

class ModuleRepository(
    private val moduleDao: ModuleDao,
    private val timetableDao: TimetableDao,
    private val remoteDataSource: LmsRemoteDataSource = LmsRemoteDataSource()
) {

    val allModules: Flow<List<ModuleEntity>> = moduleDao.getAllModules()
    val allClasses: Flow<List<TimetableEntity>> = timetableDao.getAllClasses()

    suspend fun syncModulesFromLms(userEmail: String, idToken: String? = null) {
        try {
            // 1. Fetch the raw HTML from Supabase
            val htmlContent = remoteDataSource.fetchHtmlContent(userEmail, "modules")

            if (!htmlContent.isNullOrBlank()) {
                // 2. Parse the HTML locally using Jsoup
                val doc = Jsoup.parse(htmlContent)
                val courseLinks = doc.select("a[href*='/course/view.php?id=']")
                val parsedModules = mutableListOf<ModuleEntity>()

                for (link in courseLinks) {
                    val rawText = link.text().trim()
                    if (rawText.isNotBlank() && parsedModules.none { it.moduleCode == rawText.take(10) }) {
                        parsedModules.add(
                            ModuleEntity(
                                moduleCode = rawText.take(15), // Simple extraction
                                moduleName = rawText,
                                lecturerName = "TBA",
                                isFavourite = false
                            )
                        )
                    }
                }

                // 3. Save to the local Room Database
                if (parsedModules.isNotEmpty()) {
                    for (module in parsedModules) {
                        val existing = moduleDao.getModuleByCode(module.moduleCode)
                        if (existing != null) {
                            moduleDao.insertModule(module.copy(isFavourite = existing.isFavourite))
                        } else {
                            moduleDao.insertModule(module)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun insert(module: ModuleEntity) = moduleDao.insertModule(module)

    suspend fun insertAll(modules: List<ModuleEntity>) = moduleDao.insertModules(modules)

    suspend fun toggleFavourite(code: String, isFavourite: Boolean) = moduleDao.setFavourite(code, isFavourite)

    suspend fun delete(module: ModuleEntity) = moduleDao.deleteModule(module)

    suspend fun updateReminder(id: Int, minutes: Int) = timetableDao.updateReminder(id, minutes)
}