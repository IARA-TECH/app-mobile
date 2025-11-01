package com.mobile.app_iara.data.remote.service

import com.mobile.app_iara.data.model.request.DailyActiveUsersRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface DailyActiveUsersService {
    @POST("daily-active-users")
    suspend fun registerDailyActiveUsers(@Body request: DailyActiveUsersRequest): Response<Unit>
}