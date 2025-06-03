package com.example.roblocks.blockly

import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.webkit.WebViewAssetLoader
import java.io.IOException

/**
 * Custom WebViewClient that handles asset loading and intercepts requests
 */
class BlocklyWebViewClient(private val assetLoader: WebViewAssetLoader) : WebViewClient() {
    
    override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest
    ): WebResourceResponse? {
        val response = assetLoader.shouldInterceptRequest(request.url)
        
        if (response != null) {
            Log.d(TAG, "Loading asset: ${request.url}")
            return response
        }
        
        Log.d(TAG, "Unable to find asset: ${request.url}")
        return super.shouldInterceptRequest(view, request)
    }
    
    override fun onPageFinished(view: WebView, url: String) {
        super.onPageFinished(view, url)
        Log.d(TAG, "Page loaded: $url")

        // Notify that the page has finished loading
        // Wait a bit to ensure all Blockly scripts are initialized
        view.postDelayed({
            view.evaluateJavascript("javascript:(function() { return window.Blockly ? 'ready' : 'not_ready'; })();") { result ->
                Log.d(TAG, "Blockly initialization check: $result")
                if (result.contains("ready")) {
                    // Blockly is ready, notify BlocklyBridge
                    view.evaluateJavascript("javascript:(function() { if (window.BlocklyBridge) { window.BlocklyBridge.notifyWebViewReady(); } })();", null)
                    Log.d(TAG, "WebView reported as ready for XML loading")
                }
            }
        }, 500) // 500ms delay to ensure scripts are loaded

        // Inject JavaScript to add custom event handlers for Save and Upload buttons
        // This connects the BlocklyDuino UI with our Android app
        val injectScript = """
            javascript:(function() {
                console.log('Initializing BlocklyDuino for Android');
                
                // Override save function
                window.save = function() {
                    console.log('Save XML button clicked');
                    var xml = Blockly.Xml.workspaceToDom(Blockly.mainWorkspace);
                    var xmlText = Blockly.Xml.domToText(xml);
                    if (window.BlocklyBridge) {
                        window.BlocklyBridge.saveXml(xmlText);
                    }
                };
                
                // Override upload function
                window.uploadClick = function() {
                    console.log('Upload button clicked');
                    if (window.BlocklyBridge) {
                        window.BlocklyBridge.uploadCode();
                    }
                };
                
                console.log('BlocklyDuino initialization complete');
            })();
        """.trimIndent()
        
        view.evaluateJavascript(injectScript) { result ->
            Log.d(TAG, "Injected custom functions, result: $result")
        }
    }
    
    companion object {
        private const val TAG = "BlocklyWebViewClient"
    }
} 