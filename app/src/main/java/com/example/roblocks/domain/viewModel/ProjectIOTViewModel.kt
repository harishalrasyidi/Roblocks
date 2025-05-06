package com.example.roblocks.domain.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.roblocks.data.entities.ProjectIOTEntity
import com.example.roblocks.domain.repository.ProjectIOTRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ProjectIOTViewModel @Inject constructor(
    application: Application,
    private val repository: ProjectIOTRepository
) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext
    // UI State
    private val _uiState = MutableStateFlow(BlocklyUiState())
    val uiState: StateFlow<BlocklyUiState> = _uiState.asStateFlow()
    
    // Current workspace and generated code
    private var currentWorkspaceXml: String? = null
    private var currentGeneratedCode: String? = null
    
    // Show/hide save dialog - Keep for compatibility with JS
    fun showSaveDialog() {
        // Redirect to code preview instead of save dialog
        showCodePreview()
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
    
    // Called from BlocklyBridge when workspace is saved
    fun onWorkspaceSaved(xml: String, inoCode: String) {
        Log.d(TAG, "Workspace XML and generated code received")
        
        // Validasi konten XML sebelum disimpan
        if (xml.isBlank() || !xml.trim().startsWith("<xml")) {
            Log.e(TAG, "Invalid XML received: $xml")
            _uiState.value = _uiState.value.copy(
                showToast = true,
                toastMessage = "Error: XML data tidak valid"
            )
            return
        }
        
        // Log XML untuk debugging
        Log.d(TAG, "XML to save: ${if(xml.length > 100) xml.substring(0, 100) + "..." else xml}")
        
        currentWorkspaceXml = xml
        currentGeneratedCode = inoCode
        
        // Auto-update the preview code if dialog is already open
        if (_uiState.value.showCodePreview) {
            _uiState.value = _uiState.value.copy(
                generatedCode = inoCode
            )
        }

        // Jika proyek sudah ada (edit mode), update proyek langsung
        if (_uiState.value.currentProjectId != null) {
            updateProject(_uiState.value.currentProjectId!!, xml, inoCode)
        }
    }

    fun getAllProject(): Flow<List<ProjectIOTEntity>> {
        return(repository.getAllProjects())
    }
    
    // Save project to Room DB and internal storage
    fun saveProject(projectName: String, projectTipe: String) {
        // Jika dipanggil saat pembuatan proyek awal, workspace dan code mungkin kosong
        val initialWorkspaceXml = currentWorkspaceXml ?: "<xml xmlns=\"https://developers.google.com/blockly/xml\"></xml>"
        val initialArduinoCode = currentGeneratedCode ?: "// Arduino code for ${projectName}\n// Project type: ${projectTipe}\n\nvoid setup() {\n  // Setup code here\n}\n\nvoid loop() {\n  // Main code here\n}\n"
        
        if (projectName.isBlank()) {
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
                    name = projectName,
                    tipe = projectTipe,
                    blocklyXml = initialWorkspaceXml,
                    arduinoCode = initialArduinoCode
                )
                
                // Save Arduino code to internal storage (.ino file)
                saveArduinoCodeToFile(project, initialArduinoCode)
                
                _uiState.value = _uiState.value.copy(
                    showToast = true,
                    toastMessage = "Proyek baru tersimpan",
                    currentProjectId = project.id.toString()
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
    
    // Update project dengan workspace dan code terbaru
    fun updateProject(projectId: String, blocklyXml: String, arduinoCode: String) {
        if (blocklyXml.isBlank() || !blocklyXml.trim().startsWith("<xml")) {
            Log.e(TAG, "Invalid XML format in updateProject: $blocklyXml")
            _uiState.value = _uiState.value.copy(
                showToast = true,
                toastMessage = "Workspace tidak valid"
            )
            return
        }
        
        viewModelScope.launch {
            try {
                // Get existing project
                val existingProject = repository.getProjectById(projectId)
                
                if (existingProject != null) {
                    // Update project with new workspace and code
                    val updatedProject = existingProject.copy(
                        workspace_xml = blocklyXml,
                        updated_at = System.currentTimeMillis()
                    )
                    
                    // Log before updating
                    Log.d(TAG, "Updating project ${existingProject.id} with new XML: ${blocklyXml.substring(0, minOf(100, blocklyXml.length))}...")
                    
                    repository.updateProject(updatedProject)
                    
                    // Update Arduino code file
                    saveArduinoCodeToFile(existingProject, arduinoCode)
                    
                    _uiState.value = _uiState.value.copy(
                        showToast = true,
                        toastMessage = "Proyek berhasil diperbarui"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        showToast = true,
                        toastMessage = "Proyek tidak ditemukan"
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating project", e)
                _uiState.value = _uiState.value.copy(
                    showToast = true,
                    toastMessage = "Gagal memperbarui proyek: ${e.message}"
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
        
        try {
            writeToFile(arduinoFile, generatedCode)
            Log.d(TAG, "Arduino code saved to ${arduinoFile.absolutePath}")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving Arduino code to file", e)
            _uiState.value = _uiState.value.copy(
                showToast = true,
                toastMessage = "Error saving code: ${e.message}"
            )
        }
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
                            projectTipe = project.tipe,
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
    val projectTipe: String = "",
    val showToast: Boolean = false,
    val toastMessage: String = "",
    val showCodePreview: Boolean = false,
    val generatedCode: String = "",
    val currentProjectId: String? = null,
    val shouldLoadWorkspace: Boolean = false
)
