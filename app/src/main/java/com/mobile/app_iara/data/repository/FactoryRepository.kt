package com.mobile.app_iara.data.repository

import com.mobile.app_iara.data.model.response.FactoryResponse
import com.mobile.app_iara.data.remote.RetrofitClient
import retrofit2.Response

class FactoryRepository {
    private val factoryService = RetrofitClient.factoryService

    suspend fun getFactoryDetails(factoryId: Int): Response<FactoryResponse> {
        return factoryService.getFactoryDetails(factoryId)
    }
}