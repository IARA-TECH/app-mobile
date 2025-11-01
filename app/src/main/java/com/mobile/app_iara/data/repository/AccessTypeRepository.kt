package com.mobile.app_iara.data.repository

import com.mobile.app_iara.data.model.response.AccessTypeResponse
import com.mobile.app_iara.data.remote.RetrofitClient
import retrofit2.Response

class AccessTypeRepository {
    private val accessTypeService = RetrofitClient.accessTypeService

    suspend fun getAccessTypes(): Response<List<AccessTypeResponse>> {
        return accessTypeService.getAccessTypes()
    }

}