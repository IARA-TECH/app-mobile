package com.mobile.app_iara.data.remote.service

import com.mobile.app_iara.data.model.request.UserAccessTypeRequest
import com.mobile.app_iara.data.model.response.UserAccessTypeResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserAccessTypeService {

    @GET("user-access-types/{userId}")
    suspend fun getAccessTypeById(@Path("userId") userId: String): Response<List<UserAccessTypeResponse>>

    @POST("user-access-types")
    suspend fun createUserAccessType(@Body request: UserAccessTypeRequest):Response<UserAccessTypeResponse>

}