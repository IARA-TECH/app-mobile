package com.mobile.app_iara.data.repository

import com.mobile.app_iara.data.model.request.EmailRequest
import com.mobile.app_iara.data.remote.RetrofitClient

class UserRepository {
    private val userService = RetrofitClient.userService

    suspend fun getUserProfileByEmail(request: EmailRequest) =
        userService.getUserProfileByEmail(request)
}