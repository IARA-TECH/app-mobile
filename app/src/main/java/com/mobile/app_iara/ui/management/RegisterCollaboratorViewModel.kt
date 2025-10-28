package com.mobile.app_iara.ui.management

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mobile.app_iara.data.model.request.EmailRequest
import com.mobile.app_iara.data.model.request.RegisterCollaboratorRequest
import com.mobile.app_iara.data.model.response.AccessTypeResponse
import com.mobile.app_iara.data.model.response.GenderResponse
import com.mobile.app_iara.data.model.response.UserAccessTypeResponse
import com.mobile.app_iara.data.repository.AccessTypeRepository
import com.mobile.app_iara.data.repository.GenderRepository
import com.mobile.app_iara.data.repository.UserAccessTypeRepository
import com.mobile.app_iara.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterCollaboratorViewModel : ViewModel() {

    private val accessTypeRepository = AccessTypeRepository()
    private val userRepository = UserRepository()
    private val genderRepository = GenderRepository()

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

    private val _genders = MutableStateFlow<List<GenderResponse>>(emptyList())
    val genders: StateFlow<List<GenderResponse>> = _genders

    private val _roles = MutableStateFlow<List<AccessTypeResponse>>(emptyList())
    val roles: StateFlow<List<AccessTypeResponse>> = _roles

    private var currentUserManagerId: String? = null
    private var currentFactoryId: Int? = null

    fun loadCurrentUserData() {
        viewModelScope.launch {
            try {
                val email = FirebaseAuth.getInstance().currentUser?.email
                if (email.isNullOrEmpty()) {
                    _registerState.value = RegisterState.Error("Usuário não autenticado.")
                    return@launch
                }

                val userResponse = userRepository.getUserProfileByEmail(EmailRequest(email))
                if (!userResponse.isSuccessful || userResponse.body() == null) {
                    _registerState.value = RegisterState.Error("Falha ao carregar dados do usuário.")
                    return@launch
                }

                val userData = userResponse.body()!!
                currentUserManagerId = userData.id.toString()
                currentFactoryId = userData.factoryId

                if (currentFactoryId == null) {
                    _registerState.value = RegisterState.Error("Usuário não está associado a nenhuma fábrica.")
                    return@launch
                }

                // Carrega roles para o usuário
                loadUserAccessTypes()
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error("Erro ao carregar dados: ${e.message}")
            }
        }
    }

    fun loadGenders() {
        viewModelScope.launch {
            try {
                val genderList = genderRepository.getGenders()
                _genders.value = genderList
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error("Erro ao carregar gêneros: ${e.message}")
            }
        }
    }

    private fun loadUserAccessTypes() {
        viewModelScope.launch {
            try {
                val response = accessTypeRepository.getAccessTypes()
                if (response.isSuccessful && response.body() != null) {
                    _roles.value = response.body()!!
                } else {
                    _registerState.value = RegisterState.Error("Erro ao carregar cargos.")
                }
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error("Erro ao carregar cargos: ${e.message}")
            }
        }
    }

    fun registerCollaborator(
        name: String,
        email: String,
        password: String,
        dateOfBirth: String,
        genderId: Int,
        roleId: Int
    ) {
        viewModelScope.launch {
            try {
                if (currentUserManagerId == null || currentFactoryId == null) {
                    _registerState.value = RegisterState.Error("Dados do gerente não carregados.")
                    return@launch
                }

                val request = RegisterCollaboratorRequest(
                    name = name,
                    email = email,
                    password = password,
                    dateOfBirth = dateOfBirth,
                    user_manager_id = currentUserManagerId!!,
                    factory_id = currentFactoryId!!,
                    gender_id = genderId
                )

                val response = userRepository.registerCollaborator(request)

                if (response.isSuccessful) {
                    _registerState.value = RegisterState.Success(response.body())
                } else {
                    _registerState.value = RegisterState.Error("Erro ao registrar: ${response.message()}")
                }
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error("Erro ao registrar: ${e.message}")
            }
        }
    }
}

sealed class RegisterState {
    object Idle : RegisterState()
    data class Success(val data: Any?) : RegisterState()
    data class Error(val message: String) : RegisterState()
}