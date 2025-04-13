package com.example.roblocks.blockly

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.roblocks.data.ProjectIOTEntity
import com.example.roblocks.data.ProjectIOTRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * ViewModel class that manages Blockly/Arduino interactions and persists data
 */
class BlocklyViewModel(
    private val context: Context,
    private val repository: ProjectIOTRepository
) : ViewModel() {
    
    // UI State
    private val _uiState = MutableStateFlow(BlocklyUiState())
    val uiState: StateFlow<BlocklyUiState> = _uiState.asStateFlow()
    
    // Current workspace and generated code
    private var currentWorkspaceXml: String? = null
    private var currentGeneratedCode: String? = null
    
    // Show/hide save dialog
    fun showSaveDialog() {
        _uiState.value = _uiState.value.copy(
            showSaveDialog = true
        )
    }
    
    fun hideSaveDialog() {
        _uiState.value = _uiState.value.copy(
            showSaveDialog = false,
            projectName = "",
            projectDescription = ""
        )
    }
    
    // Show/hide code preview
    fun showCodePreview() {
        if(currentGeneratedCode != null) {
            _uiState.value = _uiState.value.copy(
                showCodePreview = true,
                generatedCode = currentGeneratedCode ?: "// Tidak ada kode yang dihasilkan"
            )
        } else {
            _uiState.value = _uiState.value.copy(
                showToast = true,
                toastMessage = "Tidak ada kode yang tersedia untuk ditampilkan"
            )
        }
    }
    
    fun hideCodePreview() {
        _uiState.value = _uiState.value.copy(
            showCodePreview = false
        )
    }
    
    // Update project details in UI state
    fun updateProjectName(name: String) {
        _uiState.value = _uiState.value.copy(
            projectName = name
        )
    }
    
    fun updateProjectDescription(description: String) {
        _uiState.value = _uiState.value.copy(
            projectDescription = description
        )
    }
    
    // Called from BlocklyBridge when workspace is saved
    fun onWorkspaceSaved(xml: String, inoCode: String) {
        Log.d(TAG, "Workspace XML and generated code received")
        currentWorkspaceXml = xml
        currentGeneratedCode = inoCode
        
        // Auto-update the preview code if dialog is already open
        if (_uiState.value.showCodePreview) {
            _uiState.value = _uiState.value.copy(
                generatedCode = inoCode
            )
        }
    }
    
    // Save project to Room DB and internal storage
    fun saveProject() {
        if (currentWorkspaceXml == null || currentGeneratedCode == null) {
            _uiState.value = _uiState.value.copy(
                showToast = true,
                toastMessage = "Tidak ada kode yang tersedia untuk disimpan"
            )
            return
        }
        
        val name = _uiState.value.projectName
        val description = _uiState.value.projectDescription
        
        if (name.isBlank()) {
            _uiState.value = _uiState.value.copy(
                showToast = true,
                toastMessage = "Nama proyek tidak boleh kosong"
            )
            return
        }
        
        viewModelScope.launch {
            try {
                // Save to Room DB and get the entity with generated filenames
                val project = repository.saveProject(
                    name = name,
                    description = description,
                    blocklyXml = currentWorkspaceXml!!,
                    arduinoCode = currentGeneratedCode!!
                )
                
                // Save Arduino code to internal storage (.ino file)
                saveArduinoCodeToFile(project, currentGeneratedCode!!)
                
                _uiState.value = _uiState.value.copy(
                    showSaveDialog = false,
                    showToast = true,
                    toastMessage = "Proyek tersimpan",
                    projectName = "",
                    projectDescription = ""
                )
                
            } catch (e: Exception) {
                Log.e(TAG, "Error saving project", e)
                _uiState.value = _uiState.value.copy(
                    showToast = true,
                    toastMessage = "Gagal menyimpan proyek: ${e.message}"
                )
            }
        }
    }
    
    // Save Arduino code to internal storage
    private fun saveArduinoCodeToFile(
        project: ProjectIOTEntity,
        generatedCode: String
    ) {
        val filesDir = context.filesDir
        
        // Save generated Arduino code (.ino file)
        val arduinoFile = File(filesDir, project.file_source_code)
        writeToFile(arduinoFile, generatedCode)
    }
    
    private fun writeToFile(file: File, content: String) {
        try {
            FileOutputStream(file).use { outputStream ->
                outputStream.write(content.toByteArray())
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error writing to file: ${file.name}", e)
            throw e
        }
    }
    
    // Load project 
    fun loadProject(projectId: String) {
        viewModelScope.launch {
            try {
                val project = repository.getProjectById(projectId)
                if (project != null) {
                    currentWorkspaceXml = project.workspace_xml
                    
                    // Load Arduino code from file
                    try {
                        val filesDir = context.filesDir
                        val arduinoFile = File(filesDir, project.file_source_code)
                        val arduinoCode = if (arduinoFile.exists()) {
                            arduinoFile.readText()
                        } else {
                            // If file doesn't exist, generate code from XML
                            "// Arduino code file not found"
                        }
                        currentGeneratedCode = arduinoCode
                        
                        // Update UI state
                        _uiState.value = _uiState.value.copy(
                            currentProjectId = projectId,
                            projectName = project.name,
                            projectDescription = project.description,
                            generatedCode = currentGeneratedCode ?: ""
                        )
                        
                        // Signal to WebView to load workspace
                        _uiState.value = _uiState.value.copy(
                            showToast = true,
                            toastMessage = "Proyek berhasil dibuka",
                            shouldLoadWorkspace = true
                        )
                    } catch (e: Exception) {
                        Log.e(TAG, "Error loading Arduino code", e)
                        _uiState.value = _uiState.value.copy(
                            showToast = true,
                            toastMessage = "Gagal membuka Arduino code: ${e.message}"
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        showToast = true,
                        toastMessage = "Proyek tidak ditemukan"
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading project", e)
                _uiState.value = _uiState.value.copy(
                    showToast = true,
                    toastMessage = "Gagal membuka proyek: ${e.message}"
                )
            }
        }
    }
    
    fun workspaceLoaded() {
        _uiState.value = _uiState.value.copy(
            shouldLoadWorkspace = false
        )
    }
    
    // Clear toast message after it's shown
    fun clearToast() {
        _uiState.value = _uiState.value.copy(
            showToast = false,
            toastMessage = ""
        )
    }
    
    fun getCurrentWorkspaceXml(): String? {
        return currentWorkspaceXml
    }
    
    companion object {
        private const val TAG = "BlocklyViewModel"
    }
}

/**
 * UI State for Blockly Editor
 */
data class BlocklyUiState(
    val showSaveDialog: Boolean = false,
    val projectName: String = "",
    val projectDescription: String = "",
    val showToast: Boolean = false,
    val toastMessage: String = "",
    val showCodePreview: Boolean = false,
    val generatedCode: String = "",
    val currentProjectId: String? = null,
    val shouldLoadWorkspace: Boolean = false
)

/**
 * Factory for creating BlocklyViewModel with dependencies
 */
class BlocklyViewModelFactory(
    private val context: Context,
    private val repository: ProjectIOTRepository
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BlocklyViewModel::class.java)) {
            return BlocklyViewModel(context, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 