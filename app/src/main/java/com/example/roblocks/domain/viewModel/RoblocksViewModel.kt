package com.example.roblocks.domain.viewModel

import androidx.lifecycle.ViewModel
import com.example.roblocks.domain.repository.RoblocksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RoblocksViewModel @Inject constructor(
    private val repository: RoblocksRepository
):ViewModel() {

}

data class RoblocksState(
    val showDialog: Boolean = false,
    val projectAdd: String = "",
    val toast: Boolean = false,
    val loadingStatus: Boolean = false,
    val session: String = "",
    val menu: Int = 0,
)