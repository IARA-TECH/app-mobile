package com.mobile.app_iara.ui.profile.configuracao

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mobile.app_iara.data.model.response.UserProfileResponse
import com.mobile.app_iara.data.model.request.EmailRequest
import com.mobile.app_iara.data.repository.UserRepository
import kotlinx.coroutines.launch

class ConfiguracaoViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val _userProfile = MutableLiveData<UserProfileResponse>()
    val userProfile: LiveData<UserProfileResponse> = _userProfile

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchUserProfile() {
        val userEmail = firebaseAuth.currentUser?.email

        if (userEmail.isNullOrEmpty()) {
            _error.value = "Usuário não autenticado ou sem e-mail."
            return
        }

        viewModelScope.launch {
            try {
                val requestBody = EmailRequest(email = userEmail)
                val response = userRepository.getUserProfileByEmail(requestBody)

                if (response.isSuccessful && response.body() != null) {
                    _userProfile.postValue(response.body())
                } else {
                    _error.postValue("Erro ao buscar dados do perfil: ${response.message()}")
                }
            } catch (e: Exception) {
                _error.postValue("Falha na conexão: Verifique sua internet.")
            }
        }
    }
}