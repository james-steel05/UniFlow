package com.example.uniflow.data.repository

import android.util.Log
import com.example.uniflow.data.local.ModuleContentEntity
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
            // --- 1. SYNC MODULES ---
            Log.d("LMS_DEBUG", "Starting Modules Sync...")
            val modulesHtml = remoteDataSource.fetchHtmlContent(userEmail, "modules")

            if (!modulesHtml.isNullOrBlank()) {
                val doc = Jsoup.parse(modulesHtml)
                val courseLinks = doc.select("a.coursename")
                val parsedModules = mutableListOf<ModuleEntity>()

                for (link in courseLinks) {
                    var fullName = link.text()
                        .replace("Course name", "")
                        .replace("Course is starred", "")
                        .replace("Course is unstarred", "")
                        .trim()

                    if (fullName.contains("Assessments", ignoreCase = true) ||
                        fullName.contains("Resource Centre", ignoreCase = true)) {
                        continue
                    }

                    val row = link.closest("div.row")
                    val rawCode = row?.selectFirst("div.text-muted div")?.text()?.trim()
                        ?: fullName.take(10)

                    // NEW: Strip the "(2026)" from the code so it matches Supabase
                    val shortCode = rawCode.replace(Regex("\\(\\d{4}\\)"), "").trim()

                    if (parsedModules.none { it.moduleCode == shortCode }) {
                        parsedModules.add(
                            ModuleEntity(
                                moduleCode = shortCode,
                                moduleName = fullName,
                                lecturerName = "TBA",
                                isFavourite = false
                            )
                        )
                    }
                }

                // Save to Room
                try {
                    for (module in parsedModules) {
                        val existing = moduleDao.getModuleByCode(module.moduleCode)
                        if (existing != null) {
                            moduleDao.insertModule(module.copy(isFavourite = existing.isFavourite))
                        } else {
                            moduleDao.insertModule(module)
                        }
                    }
                    Log.d("LMS_DEBUG", "Successfully saved ${parsedModules.size} modules to Room.")
                } catch (e: Exception) {
                    Log.e("LMS_DEBUG", "Failed to save modules to Room", e)
                }
            } else {
                Log.e("LMS_DEBUG", "Modules HTML was null or empty.")
            }

            // --- 2. SYNC TIMETABLE ---
            Log.d("LMS_DEBUG", "Starting Timetable Sync...")
            val timetableHtml = remoteDataSource.fetchHtmlContent(userEmail, "timetable")

            if (!timetableHtml.isNullOrBlank()) {
                val doc = Jsoup.parse(timetableHtml)
                val parsedClasses = mutableListOf<TimetableEntity>()

                val table = doc.selectFirst("table.myschedule-table")
                val headers = table?.select("thead th")?.map { it.text().trim() } ?: emptyList()

                val rows = table?.select("tbody tr") ?: doc.select("tbody tr")

                for (row in rows) {
                    val cells = row.select("td")
                    for (i in 1 until cells.size) {
                        val cell = cells[i]
                        val eventCard = cell.selectFirst("div.event-card")

                        if (eventCard != null) {
                            val moduleCode = eventCard.selectFirst("div.mod")?.text() ?: "Unknown"
                            val lecturer = eventCard.selectFirst("div.lect")?.text() ?: ""
                            val venue = eventCard.selectFirst("div.venue")?.text() ?: ""
                            val startTime = eventCard.selectFirst("span.tstart")?.text() ?: "00:00"
                            val endTime = eventCard.selectFirst("span.tend")?.text() ?: "00:00"

                            val dayOfWeek = headers.getOrNull(i) ?: "Unknown"

                            parsedClasses.add(
                                TimetableEntity(
                                    moduleCode = moduleCode,
                                    moduleName = "Class",
                                    dayOfWeek = dayOfWeek,
                                    startTime = startTime,
                                    endTime = endTime,
                                    venue = "$venue ($lecturer)"
                                )
                            )
                        }
                    }
                }

                // Save to Room
                try {
                    timetableDao.deleteAllClasses()
                    timetableDao.insertClasses(parsedClasses)
                    Log.d("LMS_DEBUG", "Successfully saved ${parsedClasses.size} classes to Room.")
                } catch (e: Exception) {
                    Log.e("LMS_DEBUG", "Failed to save timetable to Room", e)
                }
            } else {
                Log.e("LMS_DEBUG", "Timetable HTML was null or empty.")
            }

        } catch (e: Exception) {
            Log.e("LMS_DEBUG", "General Sync Error", e)
        }
    }

    // --- MODULE CONTENT FUNCTIONS ---

    suspend fun getModuleContent(moduleCode: String): String? {
        return moduleDao.getModuleContent(moduleCode)?.htmlContent
    }

    suspend fun syncModuleContent(moduleCode: String, userEmail: String) {
        try {
            Log.d("LMS_DEBUG", "Fetching content for module: $moduleCode")

            // Note: Make sure the item_code in Supabase exactly matches $moduleCode
            val htmlContent = remoteDataSource.fetchHtmlContent(userEmail, "module_content", moduleCode)

            if (!htmlContent.isNullOrBlank()) {
                val content = ModuleContentEntity(
                    moduleCode = moduleCode,
                    htmlContent = htmlContent
                )
                moduleDao.insertModuleContent(content)
                Log.d("LMS_DEBUG", "SUCCESS: Saved content for $moduleCode to DB.")
            } else {
                Log.e("LMS_DEBUG", "FAILED: No HTML returned for $moduleCode from Supabase.")
            }
        } catch (e: Exception) {
            Log.e("LMS_DEBUG", "Error syncing module content", e)
        }
    }

    // --- CRUD OPERATIONS ---

    suspend fun insert(module: ModuleEntity) = moduleDao.insertModule(module)
    suspend fun insertAll(modules: List<ModuleEntity>) = moduleDao.insertModules(modules)
    suspend fun toggleFavourite(code: String, isFavourite: Boolean) = moduleDao.setFavourite(code, isFavourite)
    suspend fun delete(module: ModuleEntity) = moduleDao.deleteModule(module)
    suspend fun updateReminder(id: Int, minutes: Int) = timetableDao.updateReminder(id, minutes)
}