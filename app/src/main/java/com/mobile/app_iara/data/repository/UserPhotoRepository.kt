package com.mobile.app_iara.data.repository

import com.mobile.app_iara.data.model.response.UserPhotoResponse
import com.mobile.app_iara.data.remote.RetrofitClient
import retrofit2.Response

class UserPhotoRepository {
    private val userPhotoService = RetrofitClient.userPhotoService

    suspend fun getUserPhoto(userId: String): Response<UserPhotoResponse> {
        return userPhotoService.getUserPhoto(userId)
    }
}