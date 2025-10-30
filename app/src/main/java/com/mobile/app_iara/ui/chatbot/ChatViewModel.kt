package com.mobile.app_iara.ui.chatbot

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mobile.app_iara.data.model.request.MessageRequest
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
            val response = repository.authSavedUser()
            if (response != null && response.isSuccessful && response.body() != null) {
                val sessionResponse = repository.createSession()
                if (sessionResponse.isSuccessful && sessionResponse.body() != null) {
                    _sessionId.postValue(sessionResponse.body()!!.sessionId)
                    _isReady.postValue(true)
                }
            } else {
                _isReady.postValue(false)
            }
        }
    }

    fun sendMessage(userMessage: String) {
        val id = _sessionId.value ?: return
        viewModelScope.launch {
            val response = repository.sendMessage(MessageRequest(id, userMessage))
            if (response.isSuccessful && response.body() != null) {
                _botMessage.postValue(response.body()!!.response)
            }
        }
    }
}
