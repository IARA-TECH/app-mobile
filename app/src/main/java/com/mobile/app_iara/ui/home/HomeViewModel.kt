package com.mobile.app_iara.ui.home

import android.app.Application // 1. Importar Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mobile.app_iara.data.model.request.EmailRequest
import com.mobile.app_iara.data.repository.UserRepository
import kotlinx.coroutines.launch
import androidx.core.content.edit

class HomeViewModel(private val application: Application) : AndroidViewModel(application) {

    private val repository = UserRepository()
    private val prefs = application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private val _userPhotoUrl = MutableLiveData<String?>()
    val userPhotoUrl: LiveData<String?> = _userPhotoUrl

    private val _userName = MutableLiveData<String?>()
    val userName: LiveData<String?> = _userName

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private var isNetworkCallDone = false

    fun loadUserProfileData() {
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

                val response = repository.getUserProfileByEmail(EmailRequest(email))
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
}