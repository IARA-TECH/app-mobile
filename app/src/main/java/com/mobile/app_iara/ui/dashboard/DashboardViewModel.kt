package com.mobile.app_iara.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mobile.app_iara.R
import com.mobile.app_iara.data.repository.DashboardRepository
import kotlinx.coroutines.launch
import java.time.LocalTime

private data class LocalShift(
    val name: String,
    val startsAt: LocalTime,
    val endsAt: LocalTime,
)

class DashboardViewModel(private val repository: DashboardRepository) : ViewModel() {

    private val _technicalTotal = MutableLiveData<Int>()
    val technicalTotal: LiveData<Int> = _technicalTotal

    private val _farmTotal = MutableLiveData<Int>()
    val farmTotal: LiveData<Int> = _farmTotal

    private val _comparisonTotal = MutableLiveData<Int>()
    val comparisonTotal: LiveData<Int> = _comparisonTotal

    private val _currentShiftName = MutableLiveData<String>()
    val currentShiftName: LiveData<String> = _currentShiftName

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val allShifts = listOf(
        LocalShift("Matutino", LocalTime.parse("07:00:00"), LocalTime.parse("12:00:00")),
        LocalShift("Vespertino", LocalTime.parse("12:00:00"), LocalTime.parse("18:00:00")),
        LocalShift("Noturno", LocalTime.parse("18:00:00"), LocalTime.MAX)
    )

    private fun findActiveShift(): LocalShift? {
        val now = LocalTime.now()
        return allShifts.find { shift ->
            !now.isBefore(shift.startsAt) && now.isBefore(shift.endsAt)
        }
    }

    fun fetchData(factoryId: Int) {
        val currentShift = findActiveShift()
        _currentShiftName.postValue(currentShift?.name ?: "Fora de turno")

        viewModelScope.launch {
            try {
                val response = repository.getDashboardComparatives(factoryId)
                if (response.isSuccessful && response.body() != null) {
                    val totals = response.body()!!.totals
                    _technicalTotal.postValue(totals.totalTechnicalFailures)
                    _farmTotal.postValue(totals.totalFarmCondemnations)

                    val totalSum = totals.totalTechnicalFailures + totals.totalFarmCondemnations
                    _comparisonTotal.postValue(totalSum)
                } else {
                    _error.postValue("Falha ao carregar resumos: ${response.message()}")
                }
            } catch (e: Exception) {
                _error.postValue("Erro de conexão: ${e.message}")
            }
        }
    }
}

class DashboardViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(DashboardRepository()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}