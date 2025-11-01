package com.mobile.app_iara.data.remote.service

import com.mobile.app_iara.data.model.AbacusData
import com.mobile.app_iara.data.model.request.AbacusCreateRequest
import com.mobile.app_iara.data.model.response.AbacusResponse
import com.mobile.app_iara.ui.abacus.register.Abacus
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AbacusService {
    @GET("iara/api/abacuses")
    suspend fun getAllAbacuses(): AbacusResponse

    @DELETE("iara/api/abacuses/{id}")
    suspend fun deleteAbacus(@Path("id") abacusId: String): Response<Void>

    @POST("/iara/api/abacuses")
    suspend fun createAbacus(@Body abacusRequest: AbacusCreateRequest): Response<AbacusData>
}