package com.example.roblocks.domain.viewModel

import android.annotation.SuppressLint
import android.app.Application
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roblocks.data.entities.ProjectAIEntity
import com.example.roblocks.data.entities.ProjectEntity
import com.example.roblocks.data.entities.ProjectIOTEntity
import com.example.roblocks.domain.repository.ProjectAIRepository
import com.example.roblocks.domain.repository.ProjectIOTRepository
import com.example.roblocks.domain.repository.RoblocksRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoblocksViewModel @Inject constructor(
    application: Application,
    private val mainRepository: RoblocksRepository,
    private val iotRepository: ProjectIOTRepository,
    private val aiRepository: ProjectAIRepository
):AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext
    private val _uiState = MutableStateFlow(RoblocksState())
    private val _newestProjectName = MutableStateFlow(String)
    private val _user = MutableStateFlow<FirebaseUser?>(null)
    val uiState: StateFlow<RoblocksState> = _uiState.asStateFlow()
    val user: StateFlow<FirebaseUser?> = _user.asStateFlow()
    val userEmail: String?
        get() = _user.value?.email

    fun saveProject(project: ProjectEntity){
        viewModelScope.launch {
            when(project){
                is ProjectIOTEntity -> {
                    iotRepository.insertProject(project)
                }
                is ProjectAIEntity -> {
                    aiRepository.insertProject(project)
                    _uiState.value = uiState.value.copy(newestProjectName = project.name)
                }
            }
        }
    }

    fun updateUserFromAuth() {
        _user.value = FirebaseAuth.getInstance().currentUser
    }

}

data class RoblocksState(
    val showDialog: Boolean = false,
    val projectAdd: String = "",
    val toast: Boolean = false,
    val loadingStatus: Boolean = false,
    val session: String = "",
    val menu: Int = 0,
    val newestProjectName: String = ""
)

