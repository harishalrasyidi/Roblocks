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
    }
    
    companion object {
        private const val TAG = "BlocklyWebViewClient"
    }
} 