package com.mobile.app_iara.data.remote.service

import com.mobile.app_iara.data.model.response.GenderResponse
import retrofit2.http.GET

interface GenderService {

    @GET("genders")
    suspend fun getGenders(): List<GenderResponse>
}
