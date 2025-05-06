package com.example.roblocks.domain.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roblocks.data.entities.ProjectAIEntity
import com.example.roblocks.data.entities.ProjectEntity
import com.example.roblocks.data.entities.ProjectIOTEntity
import com.example.roblocks.domain.repository.ProjectAIRepository
import com.example.roblocks.domain.repository.ProjectIOTRepository
import com.example.roblocks.domain.repository.RoblocksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoblocksViewModel @Inject constructor(
    private val mainRepository: RoblocksRepository,
    private val iotRepository: ProjectIOTRepository,
    private val aiRepository: ProjectAIRepository
):ViewModel() {
    fun saveProject(project: ProjectEntity){
        viewModelScope.launch {
            when(project){
                is ProjectIOTEntity -> {
                    iotRepository.insertProject(project)
                }
                is ProjectAIEntity -> {
                    aiRepository.insertProject(project)
                }
            }
        }
    }
}

data class RoblocksState(
    val showDialog: Boolean = false,
    val projectAdd: String = "",
    val toast: Boolean = false,
    val loadingStatus: Boolean = false,
    val session: String = "",
    val menu: Int = 0,
)