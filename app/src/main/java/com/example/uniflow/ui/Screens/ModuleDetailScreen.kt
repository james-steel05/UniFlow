package com.example.uniflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

// UPDATED: Added moduleCode parameter
@Composable
fun ModuleDetailScreen(navController: NavHostController, moduleCode: String) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Materials", "Assignments")

    // Mock logic to get the module name based on the code passed in
    val moduleName = when(moduleCode) {
        "ITMDA3-34" -> "Mobile Development"
        "SAD3-22" -> "Systems Analysis"
        "DBD3-12" -> "Database Design"
        else -> "Unknown Module"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("$moduleCode: $moduleName") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                when (selectedTab) {
                    0 -> Text("Lecture notes and slides for $moduleCode will appear here.")
                    1 -> Text("Assignment list for $moduleCode will appear here.")
                }
            }
        }
    }
}