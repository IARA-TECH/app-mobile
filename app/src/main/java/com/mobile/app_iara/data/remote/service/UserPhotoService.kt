package com.mobile.app_iara.data.remote.service

import com.mobile.app_iara.data.model.response.UserPhotoResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface UserPhotoService {

    @GET("user-photos/{id}")
    suspend fun getUserPhoto(@Path("id") userId: String): Response<UserPhotoResponse>

}