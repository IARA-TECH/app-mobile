package com.mobile.app_iara.ui.dashboard.farmcondemnation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mobile.app_iara.data.model.response.FarmCondemnationResponse
import com.mobile.app_iara.data.repository.DashboardRepository
import kotlinx.coroutines.launch

class FarmCondemnationViewModel(private val repository: DashboardRepository) : ViewModel() {

    private val _farmData = MutableLiveData<FarmCondemnationResponse>()
    val farmData: LiveData<FarmCondemnationResponse> = _farmData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchFarmData(factoryId: Int) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                val response = repository.getFarmCondemnation(factoryId)
                if (response.isSuccessful && response.body() != null) {
                    _farmData.postValue(response.body())
                } else {
                    _error.postValue("Falha ao carregar dados: ${response.message()}")
                }
            } catch (e: Exception) {
                _error.postValue("Erro de conexão: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}

class FarmCondemnationViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FarmCondemnationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FarmCondemnationViewModel(DashboardRepository()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}