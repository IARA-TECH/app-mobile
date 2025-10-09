package com.mobile.app_iara.data.remote.service

import com.mobile.app_iara.data.model.response.UserProfileResponse
import com.mobile.app_iara.data.model.request.EmailRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {

    @POST("users/by-email")
    suspend fun getUserProfileByEmail(@Body request: EmailRequest): Response<UserProfileResponse>
}