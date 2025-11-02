package com.mobile.app_iara.ui.dashboard.shiftcomparison

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mobile.app_iara.data.model.response.ShiftComparisonResponse
import com.mobile.app_iara.data.repository.DashboardRepository
import kotlinx.coroutines.launch

class ShiftComparisonViewModel(private val repository: DashboardRepository) : ViewModel() {

    private val _shiftData = MutableLiveData<ShiftComparisonResponse>()
    val shiftData: LiveData<ShiftComparisonResponse> = _shiftData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchShiftData(factoryId: Int) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                val response = repository.getShiftComparison(factoryId)
                if (response.isSuccessful && response.body() != null) {
                    _shiftData.postValue(response.body())
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

class ShiftComparisonViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShiftComparisonViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ShiftComparisonViewModel(DashboardRepository()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}