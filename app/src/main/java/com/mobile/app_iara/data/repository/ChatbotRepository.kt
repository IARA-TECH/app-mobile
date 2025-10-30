package com.mobile.app_iara.data.repository

import android.content.Context
import com.mobile.app_iara.data.model.request.AuthRequest
import com.mobile.app_iara.data.model.request.MessageRequest
import com.mobile.app_iara.data.model.request.RefreshTokenRequest
import com.mobile.app_iara.data.model.request.SessionRequest
import com.mobile.app_iara.data.model.response.AuthResponse
import com.mobile.app_iara.data.model.response.MessageResponse
import com.mobile.app_iara.data.model.response.SessionResponse
import com.mobile.app_iara.data.remote.RetrofitChatbotClient
import retrofit2.Response

class ChatbotRepository(private val context: Context) {

    private val chatbotService = RetrofitChatbotClient.chabotService

    suspend fun authUser(request: AuthRequest): Response<AuthResponse> =
        chatbotService.authUser(request)

    suspend fun createSession(): Response<SessionResponse> =
        chatbotService.createSession()

    suspend fun refreshToken(request: RefreshTokenRequest): Response<AuthResponse> {
        return chatbotService.refreshToken(request)
    }

    suspend fun deleteSession(request: SessionRequest) =
        chatbotService.deleteSession(request)

    suspend fun sendMessage(request: MessageRequest): Response<MessageResponse> =
        chatbotService.sendMessage(request)

    suspend fun authSavedUser(): Response<AuthResponse>? {
        val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val senha = prefs.getString("password", null)

        if (email.isNullOrEmpty() || senha.isNullOrEmpty()) {
            return null
        }

        val request = AuthRequest(email, senha)
        return chatbotService.authUser(request)
    }
}
