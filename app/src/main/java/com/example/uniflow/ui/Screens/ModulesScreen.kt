package com.example.uniflow.ui.Screens

import android.app.Application
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.uniflow.data.local.AppDatabase
import com.example.uniflow.data.local.ModuleEntity
import com.example.uniflow.data.repository.ModuleRepository
import com.example.uniflow.ui.Routes
import com.example.uniflow.ui.components.BottomBar
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ModulesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ModuleRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = ModuleRepository(db.moduleDao(), db.timetableDao())
    }

    val modules: StateFlow<List<ModuleEntity>> = repository.allModules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleFavourite(module: ModuleEntity) {
        viewModelScope.launch {
            repository.toggleFavourite(module.moduleCode, !module.isFavourite)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModulesScreen(
    navController: NavHostController,
    viewModel: ModulesViewModel = viewModel()
) {
    val modules by viewModel.modules.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("My Modules") }) },
        bottomBar = { BottomBar(navController, Routes.MODULES) }
    ) { padding ->
        if (modules.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No modules found. Pull to refresh or sync again.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(modules, key = { it.moduleCode }) { module ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // CLEAN THE CODE: Remove spaces and parentheses to prevent navigation crashes
                                val cleanCode = module.moduleCode
                                    .replace(" (First Block)", "")
                                    .replace(" (Second Block)", "")
                                    .trim()

                                // ENCODE THE URL: Just in case there are any other weird characters
                                val encodedCode = Uri.encode(cleanCode)

                                navController.navigate("module_detail/$encodedCode")
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = module.moduleName, style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = module.moduleCode, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = "Lecturer: ${module.lecturerName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            IconButton(onClick = { viewModel.toggleFavourite(module) }) {
                                Icon(
                                    imageVector = if (module.isFavourite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                    contentDescription = "Favourite",
                                    tint = if (module.isFavourite) Color(0xFFFFC107) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}