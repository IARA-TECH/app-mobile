package com.mobile.app_iara.data.remote.service

import com.mobile.app_iara.data.model.response.UserAccessTypeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface UserAccessTypeService {

    @GET("users-access-type/{userId}")
    suspend fun getAccessTypeById(@Path("userId") userId: Int): Response<List<UserAccessTypeResponse>>
}