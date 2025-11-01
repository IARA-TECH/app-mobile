package com.mobile.app_iara.data.model.request

import com.google.gson.annotations.SerializedName

class RefreshTokenRequest (
    @SerializedName("refresh_token")
    val refreshToken: String
)