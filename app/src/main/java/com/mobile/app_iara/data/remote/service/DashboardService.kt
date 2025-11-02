package com.mobile.app_iara.data.remote.service

import com.mobile.app_iara.data.model.response.DashboardComparisonResponse
import com.mobile.app_iara.data.model.response.FarmCondemnationResponse
import com.mobile.app_iara.data.model.response.ShiftComparisonResponse
import com.mobile.app_iara.data.model.response.TechnicalFailuresResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface DashboardService {
    @GET("dashboard/comparatives")
    suspend fun getDashboardComparatives(
        @Query("factoryId") factoryId: Int
    ): Response<DashboardComparisonResponse>

    @GET("dashboard/failures")
    suspend fun getTechnicalFailures(
        @Query("factoryId") factoryId: Int
    ): Response<TechnicalFailuresResponse>

    @GET("dashboard/shifts")
    suspend fun getShiftComparison(
        @Query("factoryId") factoryId: Int
    ): Response<ShiftComparisonResponse>

    @GET("dashboard/farms")
    suspend fun getFarmCondemnation(
        @Query("factoryId") factoryId: Int
    ): Response<FarmCondemnationResponse>
}