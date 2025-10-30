package com.mobile.app_iara.ui.chatbot

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mobile.app_iara.data.model.request.MessageRequest
import com.mobile.app_iara.data.model.request.RefreshTokenRequest
import com.mobile.app_iara.data.repository.ChatbotRepository
import kotlinx.coroutines.launch

class ChatbotViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ChatbotRepository(application.applicationContext)

    private val _sessionId = MutableLiveData<String?>()
    val sessionId: LiveData<String?> get() = _sessionId

    private val _botMessage = MutableLiveData<String>()
    val botMessage: LiveData<String> get() = _botMessage

    private val _isReady = MutableLiveData<Boolean>()
    val isReady: LiveData<Boolean> get() = _isReady

    init {
        authenticateAndStartSession()
    }

    private fun authenticateAndStartSession() {
        viewModelScope.launch {
            try {
                val response = repository.authSavedUser()

                if (response != null && response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    saveTokens(authResponse.response.accessToken, authResponse.response.refreshToken)
                    startSession(authResponse.response.accessToken)

                } else {
                    val refreshToken = getRefreshTokenFromPrefs()
                    if (!refreshToken.isNullOrEmpty()) {
                        val refreshResponse = repository.refreshToken(RefreshTokenRequest(refreshToken))
                        if (refreshResponse.isSuccessful && refreshResponse.body() != null) {
                            val newTokens = refreshResponse.body()!!
                            saveTokens(newTokens.response.accessToken, newTokens.response.refreshToken)
                            startSession(newTokens.response.accessToken)
                            return@launch
                        }
                    }
                    _isReady.postValue(false)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _isReady.postValue(false)
            }
        }
    }

    fun sendMessage(userMessage: String) {
        val id = _sessionId.value
        val accessToken = getAccessTokenFromPrefs()

        if (id == null) {
            _botMessage.postValue("Erro: Sessão não iniciada")
            return
        }

        if (accessToken == null) {
            _botMessage.postValue("Erro: Token não encontrado")
            return
        }

        viewModelScope.launch {
            try {
                val request = MessageRequest(id, userMessage)
                val response = repository.sendMessage(request, accessToken)

                if (response.isSuccessful && response.body() != null) {
                    val botResponse = response.body()!!.response.response
                    _botMessage.postValue(botResponse)
                } else {
                    val error = response.errorBody()?.string() ?: "Erro desconhecido"
                    Log.e("ChatbotVM", "Erro: $error")
                    _botMessage.postValue("Erro: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("ChatbotVM", "Exception: ${e.message}", e)
                _botMessage.postValue("Erro: ${e.message}")
            }
        }
    }

    private fun saveTokens(accessToken: String, refreshToken: String) {
        val prefs = getApplication<Application>()
            .getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        prefs.edit()
            .putString("access_token", accessToken)
            .putString("refresh_token", refreshToken)
            .apply()
    }

    private fun getRefreshTokenFromPrefs(): String? {
        val prefs = getApplication<Application>()
            .getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return prefs.getString("refresh_token", null)
    }

    private fun getAccessTokenFromPrefs(): String? {
        val prefs = getApplication<Application>()
            .getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return prefs.getString("access_token", null)
    }

    private suspend fun startSession(accessToken: String) {
        val sessionResponse = repository.createSession(accessToken)
        if (sessionResponse.isSuccessful && sessionResponse.body() != null) {
            val sessionId = sessionResponse.body()!!.response.sessionId
            Log.d("ChatbotVM", "Sessão criada! ID: $sessionId")
            _sessionId.postValue(sessionId)
            _isReady.postValue(true)
        } else {
            Log.e("ChatbotVM", "Falha ao criar sessão")
            _isReady.postValue(false)
        }
    }
}