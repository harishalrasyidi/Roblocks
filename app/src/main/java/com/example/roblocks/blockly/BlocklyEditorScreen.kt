package com.example.roblocks.blockly

import android.content.Context
import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.webkit.WebViewAssetLoader
import com.example.roblocks.data.repository.ProjectIOTRepositoryImpl
import com.example.roblocks.domain.viewModel.ProjectIOTViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlocklyEditorScreen(
    navController: NavController,
    projectId: String? = null // Optional parameter to load existing project
) {
    val viewModel: ProjectIOTViewModel = hiltViewModel()

    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Store BlocklyBridge reference
    val blocklyBridgeState = remember { mutableStateOf<BlocklyBridge?>(null) }
    
    // Load project if projectId is provided
    LaunchedEffect(projectId) {
        if (projectId != null) {
            viewModel.loadProject(projectId)
        }
    }
    
    // Load workspace if shouldLoadWorkspace is true
    LaunchedEffect(uiState.shouldLoadWorkspace) {
        if (uiState.shouldLoadWorkspace && uiState.currentProjectId != null) {
            // Load workspace XML into WebView
            blocklyBridgeState.value?.loadWorkspaceFromXml(viewModel.getCurrentWorkspaceXml() ?: "")
            viewModel.workspaceLoaded()
        }
    }
    
    // Show toast as Snackbar
    LaunchedEffect(uiState.showToast) {
        if (uiState.showToast) {
            snackbarHostState.showSnackbar(uiState.toastMessage)
            viewModel.clearToast()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(if (uiState.currentProjectId != null) 
                        "Edit: ${uiState.projectName}" 
                        else "BlocklyDuino Editor") 
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // WebView for Blockly
            AndroidView(
                factory = { context ->
                    createWebView(context, viewModel) { bridge ->
                        blocklyBridgeState.value = bridge
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
            
            // Code Preview Dialog
            if (uiState.showCodePreview) {
                CodePreviewDialog(
                    code = uiState.generatedCode,
                    onDismiss = viewModel::hideCodePreview
                )
            }
        }
    }
}

@Composable
fun CodePreviewDialog(
    code: String,
    onDismiss: () -> Unit
) {
    val viewModel: ProjectIOTViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Arduino Code Preview",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(
                            color = Color(0xFF1E1E1E),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp)
                ) {
                    Text(
                        text = code,
                        color = Color(0xFFE0E0E0),
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { 
                            if (uiState.currentProjectId != null) {
                                viewModel.exportArduinoCode(uiState.currentProjectId!!)
                            }
                        },
                        enabled = uiState.currentProjectId != null
                    ) {
                        Text("Export ke Downloads")
                    }
                    
                    TextButton(onClick = onDismiss) {
                        Text("Tutup")
                    }
                }
            }
        }
    }
}

private fun createWebView(
    context: Context,
    viewModel: ProjectIOTViewModel,
    onBridgeCreated: (BlocklyBridge) -> Unit
): WebView {
    // Configure asset loader
    val assetLoader = WebViewAssetLoader.Builder()
        .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(context))
        .build()
    
    // Create and configure WebView
    return WebView(context).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        
        // Configure WebView settings
        settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = true
            allowContentAccess = true
            loadsImagesAutomatically = true
        }
        
        // Set custom WebViewClient
        webViewClient = BlocklyWebViewClient(assetLoader)
        
        // Create and store BlocklyBridge for future use
        val bridge = BlocklyBridge(
            context = context,
            onWorkspaceSaved = { xml, inoCode -> 
                viewModel.onWorkspaceSaved(xml, inoCode) 
            },
            onShowSaveDialog = { 
                viewModel.showCodePreview() 
            },
            webView = this
        )
        
        // Add JavaScript interface for communication
        addJavascriptInterface(bridge, "BlocklyBridge")
        
        // Provide bridge to composable for loading workspace
        onBridgeCreated(bridge)
        
        // Load the Blockly HTML page from assets
        loadUrl("file:///android_asset/custom_blocklyduino.html")
    }
} 