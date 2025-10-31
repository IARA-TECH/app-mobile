package com.mobile.app_iara.ui.management

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mobile.app_iara.data.model.request.EmailRequest
import com.mobile.app_iara.data.repository.UserRepository
import com.mobile.app_iara.ui.management.collaborator.CollaboratorModal
import kotlinx.coroutines.launch

class ManagementViewModel(private val application: Application) : AndroidViewModel(application) {

    private val userRepository = UserRepository()
    private val prefs = application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private val _collaborators = MutableLiveData<List<CollaboratorModal>>()
    val collaborators: LiveData<List<CollaboratorModal>> = _collaborators

    private val _userPhotoUrl = MutableLiveData<String?>()
    val userPhotoUrl: LiveData<String?> = _userPhotoUrl

    private val _userName = MutableLiveData<String?>()
    val userName: LiveData<String?> = _userName

    private var isNetworkCallDone = false

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadCollaborators() {
        viewModelScope.launch {
            try {

                val email = FirebaseAuth.getInstance().currentUser?.email
                if (email.isNullOrEmpty()) {
                    _error.postValue("Usuário não autenticado.")
                    return@launch
                }

                val userResponse = userRepository.getUserProfileByEmail(EmailRequest(email))
                if (!userResponse.isSuccessful || userResponse.body() == null) {
                    _error.postValue("Falha ao carregar dados do usuário.")
                    return@launch
                }

                val factoryId = userResponse.body()!!.factoryId

                val collaboratorsResponse = userRepository.getUsersByFactory(factoryId)
                if (collaboratorsResponse.isSuccessful) {
                    val users = collaboratorsResponse.body()!!

                    val collaboratorList = users.map { user ->
                        CollaboratorModal(
                            id = user.id.toString(),
                            name = user.name,
                            email = user.email,
                            roleName = user.accessTypeName,
                            genderName = user.genderName,
                            urlPhoto = user.userPhotoUrl,
                            dateBirth = user.dateOfBirth,
                            userManagerId = user.userManagerId,
                            factoryId = user.factoryId,
                            genderId = user.genderId
                        )
                    }

                    _collaborators.postValue(collaboratorList)
                } else {
                    _error.postValue("Falha ao carregar colaboradores.")
                }
            } catch (e: Exception) {
                _error.postValue("Erro ao carregar colaboradores: ${e.message}")
            }
        }
    }

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
}