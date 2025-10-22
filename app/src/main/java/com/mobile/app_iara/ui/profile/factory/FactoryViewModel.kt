package com.mobile.app_iara.ui.profile.factory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mobile.app_iara.data.model.request.EmailRequest
import com.mobile.app_iara.data.model.response.FactoryResponse
import com.mobile.app_iara.data.repository.FactoryRepository
import com.mobile.app_iara.data.repository.UserRepository
import kotlinx.coroutines.launch

class FactoryViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private val factoryRepository = FactoryRepository()
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val _factoryDetails = MutableLiveData<FactoryResponse>()
    val factoryDetails: LiveData<FactoryResponse> = _factoryDetails

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchFactoryData() {
        val userEmail = firebaseAuth.currentUser?.email
        if (userEmail.isNullOrEmpty()) {
            _error.value = "Usuário não autenticado."
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val userResponse = userRepository.getUserProfileByEmail(EmailRequest(userEmail))

                if (userResponse.isSuccessful && userResponse.body() != null) {
                    val factoryId = userResponse.body()!!.factoryId

                    val factoryResponse = factoryRepository.getFactoryDetails(factoryId)
                    if (factoryResponse.isSuccessful && factoryResponse.body() != null) {
                        _factoryDetails.postValue(factoryResponse.body())
                    } else {
                        _error.postValue("Erro ao buscar dados da fábrica.")
                    }
                } else {
                    _error.postValue("Erro ao buscar perfil do usuário.")
                }
            } catch (e: Exception) {
                _error.postValue("Falha na conexão. Verifique sua internet.")
            } finally {
                _isLoading.value = false
            }
        }
    }
}