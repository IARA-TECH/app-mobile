package com.mobile.app_iara.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.app_iara.data.repository.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val repository = UserRepository()

    private val _photoUrl = MutableLiveData<String?>()
    val photoUrl: LiveData<String?> = _photoUrl

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadUserPhoto(userId: String) {
        viewModelScope.launch {
            try {
                val response = repository.getUserPhoto(userId)
                if (response.isSuccessful) {
                    _photoUrl.postValue(response.body()?.urlBlob)
                } else {
                    _error.postValue("Falha ao carregar a foto: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                _error.postValue("Erro de conexão: ${e.message}")
            }
        }
    }

    // TODO: Criar uma função para lidar com o upload da foto.
    // Exemplo:
    /*
    fun updateUserPhoto(userId: String, photoUri: Uri) {
        viewModelScope.launch {
            // Lógica para converter o Uri em um arquivo e enviar para o repositório
        }
    }
    */
}