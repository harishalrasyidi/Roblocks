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