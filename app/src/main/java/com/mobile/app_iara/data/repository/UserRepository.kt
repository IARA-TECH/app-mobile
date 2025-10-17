package com.mobile.app_iara.data.repository

import com.mobile.app_iara.data.model.request.EmailRequest
import com.mobile.app_iara.data.model.response.UserPhotoResponse
import com.mobile.app_iara.data.remote.RetrofitClient
import retrofit2.Response

class UserRepository {
    private val userService = RetrofitClient.userService

    suspend fun getUserProfileByEmail(request: EmailRequest) =
        userService.getUserProfileByEmail(request)

    suspend fun getUserPhoto(userId: String): Response<UserPhotoResponse> {
        return userService.getUserPhoto(userId)
    }

    suspend fun updateUserPhoto(userId: String): Response<UserPhotoResponse> {
        return userService.updateUserPhoto(userId)
    }
}