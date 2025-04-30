package com.example.roblocks.domain.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.roblocks.data.entities.ProjectAIEntity
import com.example.roblocks.domain.repository.ProjectAIRepository
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
class ProjectAIViewModel @Inject constructor(
    application: Application,
    private val repository: ProjectAIRepository
) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext
    private val _uiState = MutableStateFlow(BlocklyUiState())
    val uiState: StateFlow<BlocklyUiState> = _uiState.asStateFlow()

    // Current workspace and generated code
    private var currentWorkspaceXml: String? = null
    private var currentGeneratedCode: String? = null

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
        if (currentGeneratedCode != null) {
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
            projectTipe = description
        )
    }


    fun getAllProject(): Flow<List<ProjectAIEntity>> {
        return (repository.getAllProjects())
    }
}

