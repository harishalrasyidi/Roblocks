package com.example.roblocks.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.roblocks.data.AppDatabase
import com.example.roblocks.data.GoogleDriveHelper
import com.example.roblocks.data.ProjectIOTRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class BlockEditorViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "BlockEditorViewModel"
    
    // Repository and Google Drive helper
    private val repository: ProjectIOTRepository
    private val driveHelper = GoogleDriveHelper(application)
    
    // State for the UI
    private val _uiState = MutableStateFlow<BlockEditorUiState>(BlockEditorUiState.Initial)
    val uiState: StateFlow<BlockEditorUiState> = _uiState.asStateFlow()
    
    // Workspace XML
    private var workspaceXml: String? = null
    
    // Generated code
    private var generatedCode: String? = null
    
    init {
        val database = AppDatabase.getDatabase(application)
        repository = ProjectIOTRepository(database.projectIOTDao())
    }
    
    // Update the workspace when it changes
    fun updateWorkspace(xml: String) {
        workspaceXml = xml
        Log.d(TAG, "Workspace updated")
    }
    
    // Set generated code from JS
    fun setGeneratedCode(code: String) {
        // Add necessary boilerplate
        generatedCode = """
            #include <Arduino.h>
            
            void setup() {
              Serial.begin(9600);
              pinMode(LED_BUILTIN, OUTPUT);
            }
            
            void loop() {
            $code
            }
        """.trimIndent()
        
        Log.d(TAG, "Generated code: $generatedCode")
        _uiState.value = BlockEditorUiState.CodeGenerated(generatedCode!!)
    }
    
    // Save project to database
    fun saveProject(name: String, description: String, studentId: String = "default_student") {
        if (workspaceXml == null || generatedCode == null) {
            _uiState.value = BlockEditorUiState.Error("No code to save. Generate code first.")
            return
        }
        
        viewModelScope.launch {
            try {
                _uiState.value = BlockEditorUiState.Loading
                
                // Create the project
                val project = repository.createProject(
                    studentId = studentId,
                    name = name,
                    description = description,
                    blockXml = workspaceXml!!,
                    generatedCode = generatedCode!!
                )
                
                _uiState.value = BlockEditorUiState.ProjectSaved(project.id)
                Log.d(TAG, "Project saved with ID: ${project.id}")
            } catch (e: Exception) {
                Log.e(TAG, "Error saving project", e)
                _uiState.value = BlockEditorUiState.Error("Error saving project: ${e.message}")
            }
        }
    }
    
    // Reset the state
    fun resetState() {
        _uiState.value = BlockEditorUiState.Initial
    }
}

// UI states for the block editor
sealed class BlockEditorUiState {
    object Initial : BlockEditorUiState()
    object Loading : BlockEditorUiState()
    data class CodeGenerated(val code: String) : BlockEditorUiState()
    data class ProjectSaved(val projectId: String) : BlockEditorUiState()
    data class Error(val message: String) : BlockEditorUiState()
} 