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
    
    // Flag to track if WebView is ready
    private var isWebViewReady = false
    
    // Pending XML to load once WebView is ready
    private var pendingXmlWorkspace: String? = null

    // Current workspace XML
    private var currentWorkspaceXml: String? = null
    
    @JavascriptInterface
    fun saveWorkspace(xml: String, inoCode: String) {
        Log.d(TAG, "Saving workspace")
        // Validasi XML untuk memastikan namespace yang benar
        if (xml.isNotEmpty()) {
            // Log XML untuk debugging
            Log.d(TAG, "XML received: ${if(xml.length > 100) xml.substring(0, 100) + "..." else xml}")
            
            // Save current XML
            currentWorkspaceXml = xml
            
            // Kirim data ke ViewModel
            onWorkspaceSaved(xml, inoCode)
        } else {
            Log.e(TAG, "Empty XML received, cannot save workspace")
        }
    }
    
    /**
     * For compatibility with BlocklyDuino's save button
     * This function will be called from JavaScript when user clicks Save XML button
     */
    @JavascriptInterface
    fun saveXml(xmlText: String) {
        Log.d(TAG, "Saving XML from BlocklyDuino")
        // Validasi XML untuk memastikan namespace yang benar
        if (xmlText.isNotEmpty()) {
            // Log XML untuk debugging
            Log.d(TAG, "XML received: ${if(xmlText.length > 100) xmlText.substring(0, 100) + "..." else xmlText}")
            
            // Get the Arduino code too
            webView.post {
                webView.evaluateJavascript(
                    "javascript:(function() { return Blockly.Arduino.workspaceToCode(); })()",
                ) { arduinoCode ->
                    // Remove quotes that surround the returned JavaScript string
                    val cleanArduinoCode = arduinoCode.trim('"').replace("\\\"", "\"").replace("\\n", "\n")
                    onWorkspaceSaved(xmlText, cleanArduinoCode)
                }
            }
        } else {
            Log.e(TAG, "Empty XML received, cannot save XML")
        }
    }
    
    /**
     * For compatibility with BlocklyDuino's upload button
     * This function will be called from JavaScript when user clicks Upload button
     */
    @JavascriptInterface
    fun uploadCode(arduinoCode: String) {
        Log.d(TAG, "Upload code requested from BlocklyDuino")
        try {
            // Save the Arduino code for upload
            onWorkspaceSaved(currentWorkspaceXml ?: "", arduinoCode)
            // Show code preview dialog automatically
            onShowSaveDialog()
        } catch (e: Exception) {
            Log.e(TAG, "Error during upload", e)
        }
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
     * Called from JavaScript to notify that WebView is ready to receive XML
     */
    @JavascriptInterface
    fun notifyWebViewReady() {
        Log.d(TAG, "WebView reported ready from JavaScript")
        isWebViewReady = true
        
        // If there's pending XML to load, load it now
        pendingXmlWorkspace?.let { xml ->
            Log.d(TAG, "Loading pending XML workspace...")
            webView.post {
                loadWorkspaceFromXml(xml)
            }
            pendingXmlWorkspace = null
        }
    }
    
    /**
     * Loads XML workspace into Blockly
     */
    fun loadWorkspaceFromXml(xml: String) {
        if (xml.isBlank()) {
            Log.e(TAG, "Cannot load empty XML workspace")
            return
        }
        
        // If WebView is not ready yet, store XML for later loading
        if (!isWebViewReady) {
            Log.d(TAG, "WebView not ready yet, storing XML for later loading")
            pendingXmlWorkspace = xml
            return
        }
        
        val escapedXml = xml.replace("'", "\\'").replace("\n", "\\n")
        Log.d(TAG, "Loading XML into workspace: ${if(xml.length > 100) xml.substring(0, 100) + "..." else xml}")
        
        // Modified to use BlocklyDuino's way of loading XML
        val jsCode = """
            javascript:(function() {
                try {
                    if (typeof Blockly === 'undefined' || !Blockly.mainWorkspace) {
                        console.error('Blockly not initialized yet');
                        return false;
                    }
                    
                    console.log('Loading XML...');
                    var xmlDom = Blockly.Xml.textToDom('$escapedXml');
                    Blockly.mainWorkspace.clear();
                    Blockly.Xml.domToWorkspace(Blockly.mainWorkspace, xmlDom);
                    console.log('Workspace loaded successfully');
                    return true;
                } catch(e) {
                    console.error('Error loading workspace:', e);
                    return false;
                }
            })();
        """.trimIndent()
        
        Log.d(TAG, "Executing XML loading JavaScript")
        webView.post {
            webView.evaluateJavascript(jsCode) { result ->
                Log.d(TAG, "Workspace load result: $result")
                if (result == "false") {
                    // If loading failed, try again after a delay
                    Log.d(TAG, "Retrying XML load after delay...")
                    webView.postDelayed({
                        loadWorkspaceFromXml(xml)
                    }, 1000)
                }
            }
        }
    }
    
    /**
     * Trigger save workspace from Kotlin code
     */
    fun saveWorkspace() {
        Log.d(TAG, "Triggering workspace save from Kotlin")
        webView.post {
            webView.evaluateJavascript(
                """
                javascript:(function() {
                    try {
                        // Get XML
                        var xml = Blockly.Xml.domToText(Blockly.Xml.workspaceToDom(Blockly.mainWorkspace));
                        // Get Arduino code
                        var code = Blockly.Arduino.workspaceToCode();
                        // Save via Bridge
                        BlocklyBridge.saveWorkspace(xml, code);
                        return "success";
                    } catch(e) {
                        console.error('Error saving workspace:', e);
                        return "error: " + e.message;
                    }
                })();
                """.trimIndent(),
            ) { result ->
                Log.d(TAG, "Workspace save result: $result")
            }
        }
    }
    
    companion object {
        private const val TAG = "BlocklyBridge"
    }
}