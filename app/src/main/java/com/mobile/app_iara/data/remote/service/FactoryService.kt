package com.mobile.app_iara.data.remote.service

import com.mobile.app_iara.data.model.response.FactoryResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface FactoryService {

    @GET("factories/{id}")
    suspend fun getFactoryDetails(@Path("id") factoryId: Int): Response<FactoryResponse>

}