package com.example.uniflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.uniflow.ui.Routes
import com.example.uniflow.ui.components.BottomBar

// List of enrolled modules
@Composable
fun ModulesScreen(navController: NavHostController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Modules") }) },
        bottomBar = { BottomBar(navController, Routes.MODULES) }
    ) { padding ->
        val modules = listOf(
            "Module Example 1",
            "Module Example 2",
            "Module Example 3"
        )

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(modules) { module ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { navController.navigate(Routes.MODULE_DETAIL) }
                ) {
                    Text(
                        text = module,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}