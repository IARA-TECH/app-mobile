package com.mobile.app_iara.data.remote.service

import com.mobile.app_iara.data.model.request.AuthRequest
import com.mobile.app_iara.data.model.request.MessageRequest
import com.mobile.app_iara.data.model.request.RefreshTokenRequest
import com.mobile.app_iara.data.model.request.SessionRequest
import com.mobile.app_iara.data.model.response.AuthResponse
import com.mobile.app_iara.data.model.response.MessageResponse
import com.mobile.app_iara.data.model.response.SessionResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST

interface ChabotService {
    @POST("auth/login")
    suspend fun authUser(@Body request: AuthRequest): Response<AuthResponse>

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<AuthResponse>

    @POST("session")
    suspend fun createSession(
        @Header("Authorization") token: String
    ): Response<SessionResponse>

    @DELETE("session")
    suspend fun deleteSession(@Body request: SessionRequest)

    @POST("chat/")
    suspend fun sendMessage(
        @Header("Authorization") token: String,
        @Body request: MessageRequest
    ): Response<MessageResponse>
}