package com.mobile.app_iara.data.model.request

import com.google.gson.annotations.SerializedName

class DailyActiveUsersRequest (
    @SerializedName("user_id")
    val userId: String
)