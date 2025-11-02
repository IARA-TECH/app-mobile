package com.mobile.app_iara.data.repository

import com.mobile.app_iara.data.remote.RetrofitClient

class DashboardRepository {

    private val dashboardService = RetrofitClient.dashboardService

    suspend fun getDashboardComparatives(factoryId: Int) =
        dashboardService.getDashboardComparatives(factoryId)

    suspend fun getTechnicalFailures(factoryId: Int) =
        dashboardService.getTechnicalFailures(factoryId)

    suspend fun getShiftComparison(factoryId: Int) =
        dashboardService.getShiftComparison(factoryId)

    suspend fun getFarmCondemnation(factoryId: Int) =
        dashboardService.getFarmCondemnation(factoryId)
}