package com.example.roblocks.domain.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.stateIn
import com.example.roblocks.data.entities.ModuleEntity
import com.example.roblocks.data.entities.QuestionEntity
import com.example.roblocks.data.remote.BackendApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ModuleQuizViewModel @Inject constructor(
    private val backendApiService: BackendApiService
):ViewModel(){

    private val _modules = MutableStateFlow<List<ModuleEntity>>(emptyList())
    val modules: StateFlow<List<ModuleEntity>> = _modules

    private val _questions = MutableStateFlow<List<QuestionEntity>>(emptyList())
    val questions: StateFlow<List<QuestionEntity>> = _questions
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchModules() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _modules.value = backendApiService.getAllModules()
            } catch (e: Exception) {
                Log.e("ModuleViewModel", "Error fetching modules", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private val _selectedModule = MutableStateFlow<ModuleEntity?>(null)
    val selectedModule: StateFlow<ModuleEntity?> = _selectedModule

    fun fetchModuleById(id: String) {
        viewModelScope.launch {
            try {
                val response = backendApiService.getModuleById(id)
                _selectedModule.value = response
            } catch (e: Exception) {
                Log.e("ModuleQuizVM", "Error fetching module by ID", e)
            }
        }
    }

    fun fetchQuestions(moduleId: String) {
        viewModelScope.launch {
            try {
                _questions.value = backendApiService.getQuestions(moduleId)
            } catch (e: Exception) {
                Log.e("ModuleViewModel", "Error fetching questions", e)
            }
        }
    }

    fun getQuestionsByModule(moduleId: String): StateFlow<List<QuestionEntity>> {
        val filteredQuestions = questions.map { list ->
            list.filter { it.module_id == moduleId }
        }
        return filteredQuestions.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    }

}
