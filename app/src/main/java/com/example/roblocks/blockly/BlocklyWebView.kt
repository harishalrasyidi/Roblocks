package com.example.roblocks.blockly

import android.annotation.SuppressLint
import android.content.Context
import android.net.http.SslError
import android.util.AttributeSet
import android.util.Log
import android.webkit.*
import android.widget.Toast
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

private const val TAG = "BlocklyWebView"

class BlocklyWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr), DefaultLifecycleObserver {

    private var webViewClient: BlocklyWebViewClient? = null
    private var jsInterface: BlocklyJavaScriptInterface? = null
    private var blocklyEventListener: BlocklyEventListener? = null

    interface BlocklyEventListener {
        fun onWorkspaceChanged(xml: String)
        fun onCodeGenerated(code: String)
        fun onBlocklyLoaded()
        fun onBlocklyError(error: String)
    }

    fun setBlocklyEventListener(listener: BlocklyEventListener) {
        this.blocklyEventListener = listener
    }

    @SuppressLint("SetJavaScriptEnabled", "AddJavascriptInterface")
    fun initialize() {
        Log.d(TAG, "Initializing BlocklyWebView")
        
        // Configure WebView settings
        settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = true
            allowContentAccess = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            cacheMode = WebSettings.LOAD_DEFAULT
            mediaPlaybackRequiresUserGesture = false
            useWideViewPort = true
            loadWithOverviewMode = true
            
            // Debugging
            // setAppCacheEnabled(true) -- Deprecated and removed
            setGeolocationEnabled(false)
            
            // Additional settings for better performance
            // setRenderPriority(WebSettings.RenderPriority.HIGH) -- Deprecated and removed
            setSupportMultipleWindows(false)
        }
        
        // Add JavaScript interface for communication with native code
        jsInterface = BlocklyJavaScriptInterface(context, blocklyEventListener)
        addJavascriptInterface(jsInterface!!, "AndroidInterface")
        
        // Setup WebViewClient with enhanced error handling
        webViewClient = BlocklyWebViewClient(blocklyEventListener)
        setWebViewClient(webViewClient!!)
        
        // Add WebChromeClient for better logging and progress monitoring
        setWebChromeClient(object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                Log.d(TAG, "Console: ${consoleMessage.messageLevel()} [${consoleMessage.sourceId()}:${consoleMessage.lineNumber()}] ${consoleMessage.message()}")
                
                // Dapatkan JavaScript errors dari console.error
                if (consoleMessage.messageLevel() == ConsoleMessage.MessageLevel.ERROR) {
                    blocklyEventListener?.onBlocklyError("JavaScript console error: ${consoleMessage.message()}")
                }
                
                return true
            }
            
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                Log.d(TAG, "Loading progress: $newProgress%")
                if (newProgress == 100) {
                    Log.d(TAG, "Page fully loaded")
                }
            }
            
            override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                Log.d(TAG, "JS Alert: $message")
                result?.confirm()
                return true
            }
            
            // WebChromeClient tidak memiliki metode onJsError
            // Gunakan konsol dan custom event untuk melacak error
        })
        
        // Tambahkan JavaScript untuk menangkap error
        evaluateJavascript("""
            window.onerror = function(message, source, lineno, colno, error) {
                if (window.AndroidInterface) {
                    AndroidInterface.reportError("Error: " + message + " at line " + lineno);
                }
                console.error("JS Error:", message, source, lineno, error);
                return true;
            };
        """, null)
        
        // Load Blockly HTML from assets
        loadBlocklyFromAssets()
    }

    private fun loadBlocklyFromAssets() {
        try {
            Log.d(TAG, "Loading Blockly HTML from assets")
            loadUrl("file:///android_asset/blockly_editor.html")
        } catch (e: Exception) {
            Log.e(TAG, "Error loading Blockly HTML", e)
            blocklyEventListener?.onBlocklyError("Failed to load Blockly HTML: ${e.message}")
            Toast.makeText(context, "Error loading Blockly: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Generate code from the current workspace
     */
    fun generateCode() {
        Log.d(TAG, "Requesting code generation")
        evaluateJavascript("javascript:generateCode()", null)
    }

    /**
     * Load a workspace from XML
     */
    fun loadWorkspace(xml: String) {
        if (xml.isBlank()) {
            Log.w(TAG, "Cannot load empty workspace XML")
            return
        }
        
        Log.d(TAG, "Loading workspace from XML")
        // Escape single quotes for JavaScript injection
        val escapedXml = xml.replace("'", "\\'")
        evaluateJavascript("javascript:loadWorkspace('$escapedXml')", null)
    }

    /**
     * Load a custom toolbox configuration
     */
    fun loadToolbox(toolboxXml: String) {
        if (toolboxXml.isBlank()) {
            Log.w(TAG, "Cannot load empty toolbox XML")
            return
        }
        
        Log.d(TAG, "Loading custom toolbox")
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Escape single quotes and newlines for JavaScript injection
                val escapedXml = toolboxXml.replace("'", "\\'")
                    .replace("\n", "\\n")
                    .replace("\r", "")
                
                evaluateJavascript("javascript:initBlockly('$escapedXml')", null)
            } catch (e: Exception) {
                Log.e(TAG, "Error loading toolbox", e)
                blocklyEventListener?.onBlocklyError("Failed to load toolbox: ${e.message}")
            }
        }
    }

    /**
     * Load toolbox configuration from assets file
     */
    fun loadToolboxFromAssets(filename: String) {
        Log.d(TAG, "Loading toolbox from assets: $filename")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val toolboxXml = readAssetFile(context, filename)
                if (toolboxXml.isNotEmpty()) {
                    CoroutineScope(Dispatchers.Main).launch {
                        loadToolbox(toolboxXml)
                    }
                } else {
                    Log.e(TAG, "Toolbox file is empty")
                    blocklyEventListener?.onBlocklyError("Toolbox file is empty")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading toolbox from assets", e)
                blocklyEventListener?.onBlocklyError("Failed to load toolbox file: ${e.message}")
            }
        }
    }

    private fun readAssetFile(context: Context, filename: String): String {
        return try {
            val inputStream = context.assets.open(filename)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            var line: String?
            
            while (reader.readLine().also { line = it } != null) {
                stringBuilder.append(line).append("\n")
            }
            
            reader.close()
            inputStream.close()
            stringBuilder.toString()
        } catch (e: IOException) {
            Log.e(TAG, "Error reading asset file: $filename", e)
            ""
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        Log.d(TAG, "Destroying BlocklyWebView")
        destroy()
    }

    private class BlocklyWebViewClient(
        private val listener: BlocklyEventListener?
    ) : WebViewClient() {
        
        override fun onPageFinished(view: WebView, url: String) {
            Log.d(TAG, "Page finished loading: $url")
            // Wait for Blockly to fully initialize before notifying
            view.postDelayed({
                listener?.onBlocklyLoaded()
            }, 500)
        }
        
        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            Log.e(TAG, "WebView error: ${error?.description} (${error?.errorCode})")
            listener?.onBlocklyError("WebView error: ${error?.description}")
        }

        override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
            Log.e(TAG, "SSL Error: ${error?.toString()}")
            handler?.cancel() // Default to canceling the request on SSL error
            listener?.onBlocklyError("SSL error: ${error?.toString()}")
        }
        
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            // Keep navigation within WebView
            return false
        }
    }

    /**
     * JavaScript interface for communication between WebView and native code
     */
    private class BlocklyJavaScriptInterface(
        private val context: Context,
        private val listener: BlocklyEventListener?
    ) {
        /**
         * Called when workspace changes
         */
        @JavascriptInterface
        fun onWorkspaceChange(xml: String) {
            Log.d(TAG, "Workspace changed")
            CoroutineScope(Dispatchers.Main).launch {
                listener?.onWorkspaceChanged(xml)
            }
        }

        /**
         * Called when code is generated
         */
        @JavascriptInterface
        fun onCodeGenerated(code: String) {
            Log.d(TAG, "Code generated")
            CoroutineScope(Dispatchers.Main).launch {
                listener?.onCodeGenerated(code)
            }
        }
        
        /**
         * For receiving log messages from JavaScript
         */
        @JavascriptInterface
        fun log(message: String) {
            Log.d(TAG, "JS Log: $message")
        }
        
        /**
         * For reporting errors from JavaScript
         */
        @JavascriptInterface
        fun reportError(error: String) {
            Log.e(TAG, "JS Error: $error")
            CoroutineScope(Dispatchers.Main).launch {
                listener?.onBlocklyError(error)
                Toast.makeText(context, "Blockly error: $error", Toast.LENGTH_SHORT).show()
            }
        }
    }
} 