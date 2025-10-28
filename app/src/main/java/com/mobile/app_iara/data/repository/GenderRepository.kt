package com.mobile.app_iara.data.repository

import com.mobile.app_iara.data.model.response.GenderResponse
import com.mobile.app_iara.data.remote.RetrofitClient

class GenderRepository {
    private val genderService = RetrofitClient.genderService

    suspend fun getGenders(): List<GenderResponse> {
        return genderService.getGenders()
    }
}