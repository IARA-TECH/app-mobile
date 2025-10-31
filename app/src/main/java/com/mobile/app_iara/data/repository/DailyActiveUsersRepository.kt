package com.mobile.app_iara.data.repository

import com.mobile.app_iara.data.model.request.DailyActiveUsersRequest
import com.mobile.app_iara.data.remote.RetrofitClient

class DailyActiveUsersRepository {

    private val dailyActiveUsersService = RetrofitClient.dailyActiveUsersService

    suspend fun registerDailyActiveUsers(request: DailyActiveUsersRequest) =
        dailyActiveUsersService.registerDailyActiveUsers(request)
}
