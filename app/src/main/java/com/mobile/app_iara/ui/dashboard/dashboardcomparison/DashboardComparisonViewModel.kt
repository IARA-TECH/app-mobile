package com.mobile.app_iara.ui.dashboard.dashboardcomparison

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mobile.app_iara.data.model.response.DashboardComparisonResponse
import com.mobile.app_iara.data.repository.DashboardRepository
import kotlinx.coroutines.launch

class DashboardComparisonViewModel(private val repository: DashboardRepository) : ViewModel() {

    private val _comparisonData = MutableLiveData<DashboardComparisonResponse>()
    val comparisonData: LiveData<DashboardComparisonResponse> = _comparisonData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchComparisonData(factoryId: Int) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                val response = repository.getDashboardComparatives(factoryId)
                if (response.isSuccessful && response.body() != null) {
                    _comparisonData.postValue(response.body())
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

class DashboardComparisonViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardComparisonViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardComparisonViewModel(DashboardRepository()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}