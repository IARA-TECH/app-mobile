package com.mobile.app_iara.ui.management

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mobile.app_iara.data.model.request.EmailRequest
import com.mobile.app_iara.data.repository.UserRepository
import com.mobile.app_iara.ui.management.collaborator.CollaboratorModal
import kotlinx.coroutines.launch

class ManagementViewModel : ViewModel() {

    private val userRepository = UserRepository()

    private val _collaborators = MutableLiveData<List<CollaboratorModal>>()
    val collaborators: LiveData<List<CollaboratorModal>> = _collaborators

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
                if (factoryId == null) {
                    _error.postValue("Usuário não está associado a nenhuma fábrica.")
                    return@launch
                }

                val collaboratorsResponse = userRepository.getUsersByFactory(factoryId)
                if (collaboratorsResponse.isSuccessful && collaboratorsResponse.body() != null) {
                    val users = collaboratorsResponse.body()!!

                    val collaboratorList = users.map { user ->
                        CollaboratorModal(
                            id = user.id.toString(),
                            name = user.name,
                            email = user.email,
                            role ="Colaborador", // Use um valor padrão se role for null
                            urlPhoto = user.userPhotoUrl
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
}