package com.mobile.app_iara.data.repository

import com.mobile.app_iara.data.model.response.UserAccessTypeResponse
import com.mobile.app_iara.data.remote.RetrofitClient.userAccessTypeService
import retrofit2.Response

class UserAccessTypeRepository {

    suspend fun getUserAccessType(userId: String): Response<List<UserAccessTypeResponse>> {
        return userAccessTypeService.getAccessTypeById(userId)
    }
}