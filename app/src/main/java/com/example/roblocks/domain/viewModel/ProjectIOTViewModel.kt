package com.example.roblocks.domain.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.BuiltInTypeConverters
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
            projectTipe = ""
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
    
    fun updateProjectTipe(tipe: String) {
        _uiState.value = _uiState.value.copy(
            projectTipe = tipe
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

    fun getAllProject(): Flow<List<ProjectIOTEntity>> {
        return(repository.getAllProjects())
    }
    
    // Save project to Room DB and internal storage
    fun saveProject(projectName: String, projectTipe: String) {
        if (currentWorkspaceXml == null || currentGeneratedCode == null) {
            _uiState.value = _uiState.value.copy(
                showToast = true,
                toastMessage = "Tidak ada kode yang tersedia untuk disimpan"
            )
            return
        }

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
                    blocklyXml = currentWorkspaceXml!!,
                    arduinoCode = currentGeneratedCode!!
                )
                
                // Save Arduino code to internal storage (.ino file)
                saveArduinoCodeToFile(project, currentGeneratedCode!!)
                
//                _uiState.value = _uiState.value.copy(
//                    showSaveDialog = false,
//                    showToast = true,
//                    toastMessage = "Proyek tersimpan",
//                    projectName = "",
//                    projectTipe = ""
//                )
                
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
    private val _project = mutableStateOf<ProjectIOTEntity?>(null)
    fun loadProjectTest(id: String){
        viewModelScope.launch {
            val loadedProject = repository.getProjectById(id)
            _project.value = loadedProject
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
