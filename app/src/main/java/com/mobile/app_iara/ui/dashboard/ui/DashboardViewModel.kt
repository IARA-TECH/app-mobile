package com.mobile.app_iara.ui.dashboard.ui

import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mobile.app_iara.data.model.request.EmailRequest
import com.mobile.app_iara.data.repository.DashboardRepository
import com.mobile.app_iara.data.repository.UserRepository
import kotlinx.coroutines.launch
import java.time.LocalTime

private data class LocalShift(
    val name: String,
    val startsAt: LocalTime,
    val endsAt: LocalTime,
)

class DashboardViewModel(
    private val dashboardRepository: DashboardRepository,
    private val userRepository: UserRepository
) : ViewModel() {

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

    private val _userPhotoUrl = MutableLiveData<String?>()
    val userPhotoUrl: LiveData<String?> = _userPhotoUrl

    private val _userName = MutableLiveData<String?>()
    val userName: LiveData<String?> = _userName

    private var isNetworkCallDone = false


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

    fun loadUserProfileData(context: Context) {
        val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val cachedUrl = prefs.getString("user_photo_url", null)
        val cachedName = prefs.getString("user_name", null)

        _userPhotoUrl.postValue(cachedUrl)
        _userName.postValue(cachedName)

        if (isNetworkCallDone) return

        viewModelScope.launch {
            try {
                val email = FirebaseAuth.getInstance().currentUser?.email
                if (email.isNullOrEmpty()) {
                    _error.postValue("Usuário não encontrado.")
                    return@launch
                }

                val response = userRepository.getUserProfileByEmail(EmailRequest(email))
                if (response.isSuccessful && response.body() != null) {
                    val profile = response.body()!!
                    isNetworkCallDone = true

                    val newUrl = profile.userPhotoUrl
                    val newName = profile.name

                    if (newUrl != cachedUrl || newName != cachedName) {
                        _userPhotoUrl.postValue(newUrl)
                        _userName.postValue(newName)

                        prefs.edit {
                            putString("user_photo_url", newUrl)
                            putString("user_name", newName)
                        }
                    }
                } else {
                    _error.postValue("Falha ao carregar os dados do perfil.")
                }
            } catch (e: Exception) {
                _error.postValue("Erro de conexão ao carregar perfil: ${e.message}")
            }
        }
    }

    fun fetchData(factoryId: Int) {
        val currentShift = findActiveShift()
        _currentShiftName.postValue(currentShift?.name ?: "Fora de turno")

        viewModelScope.launch {
            try {
                val response = dashboardRepository.getDashboardComparatives(factoryId)
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
            return DashboardViewModel(
                dashboardRepository = DashboardRepository(),
                userRepository = UserRepository()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}