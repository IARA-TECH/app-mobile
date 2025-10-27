package com.mobile.app_iara.data.remote.service

import com.mobile.app_iara.data.model.response.AbacusResponse
import retrofit2.http.GET

interface AbacusService {
    @GET("iara/api/abacuses")
    suspend fun getAllAbacuses(): AbacusResponse
}