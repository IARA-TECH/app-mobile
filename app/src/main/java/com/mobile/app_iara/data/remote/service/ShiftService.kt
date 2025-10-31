package com.mobile.app_iara.data.remote.service

import com.mobile.app_iara.data.model.response.ShiftListResponse
import retrofit2.Response
import retrofit2.http.GET

interface ShiftService {
    @GET("iara/api/shifts")
    suspend fun getAllShifts(): Response<ShiftListResponse>
}