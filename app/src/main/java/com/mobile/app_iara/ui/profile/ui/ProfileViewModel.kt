package com.mobile.app_iara.ui.profile.ui

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mobile.app_iara.data.model.request.EmailRequest
import com.mobile.app_iara.data.model.response.UserProfileResponse
import com.mobile.app_iara.data.repository.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application){

    private val repository = UserRepository()
    private val prefs = application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private val _userProfile = MutableLiveData<UserProfileResponse>()
    val userProfile: LiveData<UserProfileResponse> = _userProfile

    private val _userPhotoUrl = MutableLiveData<String?>()
    val userPhotoUrl: LiveData<String?> = _userPhotoUrl

    private val _userName = MutableLiveData<String?>()
    val userName: LiveData<String?> = _userName

    private val _newPhotoUrl = MutableLiveData<String?>()
    val newPhotoUrl: LiveData<String?> = _newPhotoUrl

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadCurrentUserProfile() {
        val cachedUrl = prefs.getString("user_photo_url", null)
        val cachedName = prefs.getString("user_name", null)

        _userPhotoUrl.postValue(cachedUrl)
        _userName.postValue(cachedName)

        viewModelScope.launch {
            try {
                val email = FirebaseAuth.getInstance().currentUser?.email
                if (email.isNullOrEmpty()) {
                    _error.postValue("Usuário não encontrado.")
                    return@launch
                }

                val response = repository.getUserProfileByEmail(EmailRequest(email))
                if (response.isSuccessful && response.body() != null) {
                    val profile = response.body()!!
                    _userProfile.postValue(profile)

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

                        prefs.edit {
                            putString("user_photo_url", url)
                            putString("user_name", _userProfile.value?.name)
                        }
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