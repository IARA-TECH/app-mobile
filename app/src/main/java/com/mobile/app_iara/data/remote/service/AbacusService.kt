package com.mobile.app_iara.data.remote.service

import com.mobile.app_iara.data.model.AbacusData
import com.mobile.app_iara.data.model.request.AbacusCreateRequest
import com.mobile.app_iara.data.model.response.AbacusCreateResponse
import com.mobile.app_iara.data.model.response.AbacusListResponse
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface AbacusService {
    @GET("iara/api/abacuses")
    suspend fun getAllAbacuses(): AbacusListResponse

    @DELETE("iara/api/abacuses/{id}")
    suspend fun deleteAbacus(@Path("id") abacusId: String): Response<Void>

    @POST("/iara/api/abacuses")
    suspend fun createAbacus(
        @Body abacusRequest: AbacusCreateRequest
    ): Response<AbacusCreateResponse>

    @PUT("iara/api/abacuses/{id}")
    suspend fun updateAbacus(
        @Path("id") abacusId: String,
        @Body abacusRequest: AbacusCreateRequest
    ): Response<AbacusData>
}