package com.mobile.app_iara.ui.management.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.app_iara.data.model.request.UserAccessTypeRequest
import com.mobile.app_iara.data.model.request.UserProfileRequest
import com.mobile.app_iara.data.model.response.AccessTypeResponse
import com.mobile.app_iara.data.model.response.GenderResponse
import com.mobile.app_iara.ui.management.collaborator.CollaboratorModal
import com.mobile.app_iara.data.repository.AccessTypeRepository
import com.mobile.app_iara.data.repository.GenderRepository
import com.mobile.app_iara.data.repository.UserAccessTypeRepository
import com.mobile.app_iara.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditCollaboratorViewModel : ViewModel() {

    private val accessTypeRepository = AccessTypeRepository()
    private val userRepository = UserRepository()
    private val genderRepository = GenderRepository()
    private val userAccessTypeRepository = UserAccessTypeRepository()

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState

    private val _genders = MutableStateFlow<List<GenderResponse>>(emptyList())
    val genders: StateFlow<List<GenderResponse>> = _genders

    private val _roles = MutableStateFlow<List<AccessTypeResponse>>(emptyList())
    val roles: StateFlow<List<AccessTypeResponse>> = _roles

    fun loadGenders() {
        viewModelScope.launch {
            try {
                val genderList = genderRepository.getGenders()
                _genders.value = genderList
            } catch (e: Exception) {
                _updateState.value = UpdateState.Error("Erro ao carregar gêneros: ${e.message}")
            }
        }
    }

    fun loadUserAccessTypes() {
        viewModelScope.launch {
            try {
                val response = accessTypeRepository.getAccessTypes()
                if (response.isSuccessful && response.body() != null) {
                    _roles.value = response.body()!!
                } else {
                    _updateState.value = UpdateState.Error("Erro ao carregar cargos.")
                }
            } catch (e: Exception) {
                _updateState.value = UpdateState.Error("Erro ao carregar cargos: ${e.message}")
            }
        }
    }

    fun updateCollaborator(
        oldData: CollaboratorModal,
        newGenderId: Int,
        newRoleId: Int
    ) {
        viewModelScope.launch {
            _updateState.value = UpdateState.Loading

            try {
                val profileRequest = UserProfileRequest(
                    name = oldData.name,
                    email = oldData.email,
                    password = null,
                    dateOfBirth = oldData.dateBirth,
                    userManagerId = oldData.userManagerId,
                    factoryId = oldData.factoryId,
                    genderId = newGenderId
                )

                val profileResponse = userRepository.updateCollaborator(profileRequest, oldData.id)

                if (!profileResponse.isSuccessful) {
                    _updateState.value =
                        UpdateState.Error("Falha ao atualizar perfil: ${profileResponse.message()}")
                    return@launch
                }

                val accessRequest = UserAccessTypeRequest(
                    accessTypeId = newRoleId,
                    userId = oldData.id
                )

                val accessResponse = userAccessTypeRepository.createUserAccessType(accessRequest)

                if (!accessResponse.isSuccessful) {
                    _updateState.value =
                        UpdateState.Error("Perfil atualizado, mas falha ao definir novo cargo: ${accessResponse.message()}")
                    return@launch
                }

                _updateState.value = UpdateState.Success

            } catch (e: Exception) {
                _updateState.value = UpdateState.Error("Erro ao atualizar: ${e.message}")
            }
        }
    }

    fun resetUpdateState() { _updateState.value = UpdateState.Idle
    }
}

sealed class UpdateState {
    object Idle : UpdateState()
    object Loading : UpdateState()
    object Success : UpdateState()
    data class Error(val message: String) : UpdateState()
}