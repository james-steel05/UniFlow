package com.example.uniflow.ui.screens

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.uniflow.data.local.ModuleEntity
import com.example.uniflow.ui.Routes
import com.example.uniflow.ui.components.BottomBar

@Composable
fun ModulesScreen(
    navController: NavHostController,
    viewModel: ModulesViewModel = viewModel()
) {
    // Observe the database
    val modules by viewModel.modules.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Modules") }) },
        bottomBar = { BottomBar(navController, Routes.MODULES) }
    ) { padding ->
        if (modules.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No modules found. Check your database connection.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(modules, key = { it.moduleCode }) { module ->
                    ModuleCard(
                        module = module,
                        onFavouriteClick = {
                            // Toggle favorite in DB
                            viewModel.toggleFavourite(module.moduleCode, module.isFavourite)
                        },
                        onClick = { navController.navigate("${Routes.MODULE_DETAIL}/${module.moduleCode}") }
                    )
                }
            }
        }
    }
}

@Composable
fun ModuleCard(
    module: ModuleEntity,
    onFavouriteClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = module.moduleName, style = MaterialTheme.typography.titleMedium)
                Text(text = "Code: ${module.moduleCode}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Lecturer: ${module.lecturerName}", style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onFavouriteClick) {
                Icon(
                    imageVector = if (module.isFavourite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = "Favourite",
                    tint = if (module.isFavourite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}