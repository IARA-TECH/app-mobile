package com.mobile.app_iara.data.remote.service

import com.mobile.app_iara.data.model.response.AccessTypeResponse
import retrofit2.Response
import retrofit2.http.GET

interface AccessTypeService {
    @GET("access-types")
    suspend fun getAccessTypes(): Response<List<AccessTypeResponse>>
}