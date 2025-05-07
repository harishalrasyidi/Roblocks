package com.example.roblocks.domain.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImagePainter
import com.example.roblocks.data.entities.ProjectAIEntity
import com.example.roblocks.domain.repository.ProjectAIRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
import javax.inject.Inject

@HiltViewModel
class ProjectAIViewModel @Inject constructor(
    application: Application,
    private val repository: ProjectAIRepository
) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext
    private val _uiState = MutableStateFlow(ProjectAIUiState())
    val uiState: StateFlow<ProjectAIUiState> = _uiState.asStateFlow()


    fun saveAIProject(
        project: ProjectAIEntity,
        modelFile: File,
        datasetDir: File,
        onSuccess: (File) -> Unit,
        onError: (Throwable) -> Unit
    ){
        viewModelScope.launch {
            try {
                val zipFile = repository.exportProjectAsZip(context, project, modelFile, datasetDir)
                onSuccess(zipFile)
            }catch (e: Exception){
                onError(e)
            }
        }
    }

    fun loadProject(projectId: String) {
        viewModelScope.launch {
            try {
                val project = repository.getProjectById(projectId)
                if (project != null) {
                    try{
                        _uiState.value = _uiState.value.copy(
                            currentProjectId = projectId,
                            projectName = project.name,
                            projectTipe = project.tipe,
                        )

                        _uiState.value = _uiState.value.copy(
                            showToast = true,
                            toastMessage = "Proyek berhasil dibuka",
                            shouldLoadWorkspace = true
                        )
                    } catch (e: Exception) {
                        Log.e(TAG, "Error ", e)
                        _uiState.value = _uiState.value.copy(
                            showToast = true,
                            toastMessage = "Gagal membuka project AI: ${e.message}"
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
            projectTipe = description
        )
    }


    fun getAllProject(): Flow<List<ProjectAIEntity>> {
        return (repository.getAllProjects())
    }

    fun deleteProjectByID(id: String) {
        viewModelScope.launch {
            repository.deleteProjectByID(id)
        }
    }
}

data class ProjectAIUiState(
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

