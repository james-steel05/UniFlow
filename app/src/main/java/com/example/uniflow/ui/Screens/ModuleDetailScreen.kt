package com.example.uniflow.ui.Screens

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.uniflow.MainActivity

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleDetailScreen(
    navController: NavHostController,
    moduleCode: String,
    viewModel: ModuleDetailViewModel = viewModel()
) {
    val contentState by viewModel.contentState.collectAsState()

    // Load content when screen opens
    LaunchedEffect(moduleCode) {
        viewModel.loadContent(moduleCode, MainActivity.currentUserEmail.value)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(moduleCode) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Refresh button
                    IconButton(onClick = {
                        // This line was causing the error, now it works
                        viewModel.loadContent(moduleCode, MainActivity.currentUserEmail.value, forceRefresh = true)
                    }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = contentState) {
                is ContentState.Loading -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Downloading module content...")
                    }
                }
                is ContentState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            viewModel.loadContent(moduleCode, MainActivity.currentUserEmail.value, forceRefresh = true)
                        }) {
                            Text("Try Again")
                        }
                    }
                }
                is ContentState.Success -> {
                    // Render the downloaded HTML
                    AndroidView(
                        factory = { context ->
                            WebView(context).apply {
                                webViewClient = WebViewClient()
                                settings.javaScriptEnabled = true
                                settings.domStorageEnabled = true
                                settings.loadWithOverviewMode = true
                                settings.useWideViewPort = true

                                // This CSS hides the Eduvos Moodle navigation and makes it fit the phone screen
                                val injectedCss = """
                                    <style>
                                        /* Hide Moodle navigation, headers, footers, and sidebars */
                                        #page-header, #page-footer, .navbar, .breadcrumb, .block, #nav-drawer, .drawer, .header-maxwidth, .page-context-header { 
                                            display: none !important; 
                                        }
                                        body { 
                                            padding: 16px !important; 
                                            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif !important; 
                                            background-color: #ffffff;
                                            color: #333333;
                                        }
                                        img { 
                                            max-width: 100% !important; 
                                            height: auto !important; 
                                        }
                                        table { 
                                            width: 100% !important; 
                                            border-collapse: collapse !important; 
                                            display: block;
                                            overflow-x: auto;
                                        }
                                        td, th { 
                                            border: 1px solid #ddd !important; 
                                            padding: 8px !important; 
                                        }
                                        a { color: #0066cc; }
                                    </style>
                                """

                                // Inject our CSS into the <head> of the HTML
                                val modifiedHtml = state.html.replace("<head>", "<head>$injectedCss")

                                loadDataWithBaseURL(null, modifiedHtml, "text/html", "UTF-8", null)
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}