package com.mobile.app_iara.ui.dashboard.technicalfailures

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mobile.app_iara.data.model.response.TechnicalFailuresResponse
import com.mobile.app_iara.data.repository.DashboardRepository
import kotlinx.coroutines.launch

class TechnicalFailuresViewModel(private val repository: DashboardRepository) : ViewModel() {

    private val _failuresData = MutableLiveData<TechnicalFailuresResponse>()
    val failuresData: LiveData<TechnicalFailuresResponse> = _failuresData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchTechnicalFailures(factoryId: Int) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                val response = repository.getTechnicalFailures(factoryId)
                if (response.isSuccessful && response.body() != null) {
                    _failuresData.postValue(response.body())
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

class TechnicalFailuresViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TechnicalFailuresViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TechnicalFailuresViewModel(DashboardRepository()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}