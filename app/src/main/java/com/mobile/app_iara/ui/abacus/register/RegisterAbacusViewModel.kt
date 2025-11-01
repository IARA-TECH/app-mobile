package com.mobile.app_iara.ui.abacus.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.app_iara.data.model.AbacusData
import com.mobile.app_iara.data.model.request.AbacusCreateRequest
import com.mobile.app_iara.data.repository.AbacusRepository
import kotlinx.coroutines.launch

class RegisterAbacusViewModel (private val repository: AbacusRepository) : ViewModel() {

    fun registerAbacus(
        abacus: AbacusCreateRequest,
        onSuccess: (AbacusData) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.registerAbacus(abacus)

            result.onSuccess { registeredAbacusData ->
                onSuccess(registeredAbacusData)
            }.onFailure { error ->
                onFailure(error)
            }
        }
    }
}