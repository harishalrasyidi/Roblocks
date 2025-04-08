package com.example.roblocks.blockly

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.roblocks.R
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter

private const val TAG = "BlocklyActivity"

class BlocklyActivity : AppCompatActivity(), BlocklyWebView.BlocklyEventListener {
    
    private lateinit var blocklyWebView: BlocklyWebView
    private lateinit var progressBar: ProgressBar
    private var lastGeneratedCode: String = ""
    private var currentWorkspaceXml: String = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blockly)
        
        // Set title in ActionBar
        supportActionBar?.apply {
            title = "Blockly IoT Editor"
            setDisplayHomeAsUpEnabled(true)
        }
        
        // Initialize UI components
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
        
        // Setup buttons
        findViewById<Button>(R.id.btnGenerateCode).setOnClickListener {
            generateCode()
        }
        
        findViewById<Button>(R.id.btnSaveWorkspace).setOnClickListener {
            saveWorkspace()
        }
        
        // Create and add WebView to container
        val webViewContainer = findViewById<FrameLayout>(R.id.webViewContainer)
        blocklyWebView = BlocklyWebView(this)
        webViewContainer.addView(blocklyWebView)
        
        // Setup BlocklyWebView
        blocklyWebView.setBlocklyEventListener(this)
        lifecycle.addObserver(blocklyWebView)
        
        // Initialize the WebView
        blocklyWebView.initialize()
    }
    
    override fun onBackPressed() {
        // Check if we should handle the back press differently (e.g., save workspace)
        if (currentWorkspaceXml.isNotEmpty()) {
            showSaveWorkspaceDialog()
        } else {
            super.onBackPressed()
        }
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    // Generate Arduino code from blocks
    fun generateCode() {
        blocklyWebView.generateCode()
    }
    
    // Save current workspace
    fun saveWorkspace() {
        if (currentWorkspaceXml.isEmpty()) {
            Toast.makeText(this, "No workspace to save", Toast.LENGTH_SHORT).show()
            return
        }
        
        lifecycleScope.launch {
            try {
                val filename = "workspace_${System.currentTimeMillis()}.xml"
                val file = File(getExternalFilesDir(null), filename)
                FileWriter(file).use { it.write(currentWorkspaceXml) }
                Toast.makeText(this@BlocklyActivity, "Workspace saved to $filename", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e(TAG, "Error saving workspace", e)
                Toast.makeText(this@BlocklyActivity, "Error saving workspace: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    // Load workspace from a file
    fun loadWorkspace(file: File) {
        try {
            val xml = file.readText()
            blocklyWebView.loadWorkspace(xml)
            Toast.makeText(this, "Workspace loaded successfully", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e(TAG, "Error loading workspace", e)
            Toast.makeText(this, "Error loading workspace: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    // Load the IoT toolbox
    fun loadIoTToolbox() {
        blocklyWebView.loadToolboxFromAssets("iot_toolbox.xml")
    }
    
    // Show dialog to save workspace before exiting
    private fun showSaveWorkspaceDialog() {
        // In a real app, show a dialog and act based on user choice
        // For simplicity, we're just finishing the activity here
        finish()
    }
    
    // BlocklyEventListener implementation
    override fun onWorkspaceChanged(xml: String) {
        currentWorkspaceXml = xml
        Log.d(TAG, "Workspace changed: ${xml.take(100)}...")
    }
    
    override fun onCodeGenerated(code: String) {
        lastGeneratedCode = code
        Log.d(TAG, "Code generated: $code")
        Toast.makeText(this, "Code generated successfully", Toast.LENGTH_SHORT).show()
        
        // Here you could send the code to another component
        // or save it to a file
    }
    
    override fun onBlocklyLoaded() {
        Log.d(TAG, "Blockly loaded successfully")
        // Hide progress bar
        progressBar.visibility = View.GONE
        // Load the IoT toolbox when Blockly is ready
        loadIoTToolbox()
    }
    
    override fun onBlocklyError(error: String) {
        Log.e(TAG, "Blockly error: $error")
        // Hide progress bar on error too
        progressBar.visibility = View.GONE
        Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
    }
}