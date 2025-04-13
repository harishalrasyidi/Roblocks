package com.example.roblocks.blockly

import android.content.Context
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView

/**
 * Bridge class for communication between JavaScript in WebView and Kotlin
 */
class BlocklyBridge(
    private val context: Context,
    private val onWorkspaceSaved: (xml: String, inoCode: String) -> Unit,
    private val onShowSaveDialog: () -> Unit,
    private val webView: WebView
) {
    
    @JavascriptInterface
    fun saveWorkspace(xml: String, inoCode: String) {
        Log.d(TAG, "Saving workspace")
        onWorkspaceSaved(xml, inoCode)
    }
    
    @JavascriptInterface
    fun showSaveDialog() {
        Log.d(TAG, "Showing save dialog")
        onShowSaveDialog()
    }
    
    @JavascriptInterface
    fun logMessage(message: String) {
        Log.d(TAG, "JS: $message")
    }
    
    @JavascriptInterface
    fun notifyWorkspaceLoaded() {
        Log.d(TAG, "Workspace loaded")
        // Method to notify Kotlin that workspace was loaded
    }
    
    /**
     * Loads XML workspace into Blockly
     */
    fun loadWorkspaceFromXml(xml: String) {
        val escapedXml = xml.replace("'", "\\'").replace("\n", "\\n")
        val jsCode = "javascript:loadBlocksFromXml('$escapedXml');"
        
        Log.d(TAG, "Loading workspace from XML")
        webView.post {
            webView.evaluateJavascript(jsCode) { result ->
                Log.d(TAG, "Workspace load result: $result")
            }
        }
    }
    
    companion object {
        private const val TAG = "BlocklyBridge"
    }
} 