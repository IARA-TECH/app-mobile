package com.mobile.app_iara.ui.profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mobile.app_iara.data.model.request.EmailRequest
import com.mobile.app_iara.data.model.response.UserProfileResponse
import com.mobile.app_iara.data.repository.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val repository = UserRepository()

    private val _userProfile = MutableLiveData<UserProfileResponse>()
    val userProfile: LiveData<UserProfileResponse> = _userProfile

    private val _newPhotoUrl = MutableLiveData<String?>()
    val newPhotoUrl: LiveData<String?> = _newPhotoUrl

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadCurrentUserProfile() {
        viewModelScope.launch {
            try {
                val email = FirebaseAuth.getInstance().currentUser?.email
                if (email.isNullOrEmpty()) {
                    _error.postValue("Usuário não encontrado.")
                    return@launch
                }

                val response = repository.getUserProfileByEmail(EmailRequest(email))
                if (response.isSuccessful && response.body() != null) {
                    _userProfile.postValue(response.body())
                } else {
                    _error.postValue("Falha ao carregar os dados do perfil.")
                }
            } catch (e: Exception) {
                _error.postValue("Erro de conexão ao carregar perfil: ${e.message}")
            }
        }
    }

    fun updateUserPhoto(context: Context, photoUri: Uri) {
        val databaseUserId = _userProfile.value?.id
        if (databaseUserId == null) {
            _error.postValue("ID do usuário não carregado. Tente novamente.")
            return
        }

        viewModelScope.launch {
            try {
                val url = repository.uploadUserPhoto(context, databaseUserId, photoUri)

                if (url != null) {
                    val saveSuccess = repository.saveUserPhotoUrl(databaseUserId, url)
                    if (saveSuccess) {
                        _newPhotoUrl.postValue(url)
                    } else {
                        _error.postValue("Falha ao salvar a foto no perfil.")
                    }
                } else {
                    _error.postValue("Falha ao fazer upload da imagem.")
                }
            } catch (e: Exception) {
                _error.postValue("Erro ao enviar foto: ${e.message}")
            }
        }
    }
}