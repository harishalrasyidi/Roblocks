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
        // Validasi XML untuk memastikan namespace yang benar
        if (xml.isNotEmpty()) {
            // Log XML untuk debugging
            Log.d(TAG, "XML received: ${if(xml.length > 100) xml.substring(0, 100) + "..." else xml}")
            
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
    fun uploadCode() {
        Log.d(TAG, "Upload code requested from BlocklyDuino")
        webView.post {
            webView.evaluateJavascript(
                "javascript:(function() { return { xml: Blockly.Xml.domToText(Blockly.Xml.workspaceToDom(Blockly.mainWorkspace)), code: Blockly.Arduino.workspaceToCode() }; })()",
            ) { result ->
                try {
                    // Parse the result which comes as a JavaScript object string
                    // Basic parsing: the result is in the format: {"xml":"...","code":"..."}
                    val xml = result.substringAfter("\"xml\":\"").substringBefore("\",\"code\"")
                    val code = result.substringAfter("\"code\":\"").substringBefore("\"}")
                    
                    // Clean up escaped characters
                    val cleanXml = xml.replace("\\\"", "\"").replace("\\n", "\n")
                    val cleanCode = code.replace("\\\"", "\"").replace("\\n", "\n")
                    
                    onWorkspaceSaved(cleanXml, cleanCode)
                    // Show code preview dialog automatically
                    onShowSaveDialog()
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing upload result", e)
                }
            }
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
     * Loads XML workspace into Blockly
     */
    fun loadWorkspaceFromXml(xml: String) {
        val escapedXml = xml.replace("'", "\\'").replace("\n", "\\n")
        // Modified to use BlocklyDuino's way of loading XML
        val jsCode = """
            javascript:(function() {
                try {
                    var xmlDom = Blockly.Xml.textToDom('$escapedXml');
                    Blockly.mainWorkspace.clear();
                    Blockly.Xml.domToWorkspace(Blockly.mainWorkspace, xmlDom);
                    console.log('Workspace loaded successfully');
                } catch(e) {
                    console.error('Error loading workspace:', e);
                }
            })();
        """.trimIndent()
        
        Log.d(TAG, "Loading workspace from XML")
        webView.post {
            webView.evaluateJavascript(jsCode) { result ->
                Log.d(TAG, "Workspace load result: $result")
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