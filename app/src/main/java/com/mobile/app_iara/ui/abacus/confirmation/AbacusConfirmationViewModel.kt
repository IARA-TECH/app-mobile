package com.mobile.app_iara.ui.abacus.confirmation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.app_iara.data.model.response.AbacusConfirmData
import com.mobile.app_iara.data.repository.AbacusPhotoRepository
import com.mobile.app_iara.data.repository.AbacusRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ConfirmationState {
    object Idle : ConfirmationState()
    object Loading : ConfirmationState()
    data class Success(val data: AbacusConfirmData) : ConfirmationState()
    data class Error(val message: String) : ConfirmationState()
}

class AbacusConfirmationViewModel : ViewModel() {

    private val repository = AbacusPhotoRepository()

    private val _uiState = MutableStateFlow<ConfirmationState>(ConfirmationState.Idle)
    val uiState: StateFlow<ConfirmationState> = _uiState

    fun confirmUpload(
        context: Context,
        factoryId: Int,
        shiftId: String,
        takenBy: String,
        abacusId: String,
        imageUriString: String,
        csvData: String
    ) {
        _uiState.value = ConfirmationState.Loading
        viewModelScope.launch {
            val result = repository.confirmAndUploadData(
                context, factoryId, shiftId, takenBy, abacusId, imageUriString, csvData
            )

            result.onSuccess { data ->
                _uiState.value = ConfirmationState.Success(data)
            }.onFailure { error ->
                _uiState.value = ConfirmationState.Error(error.message ?: "Erro desconhecido")
            }
        }
    }

    fun resetState() {
        _uiState.value = ConfirmationState.Idle
    }
}