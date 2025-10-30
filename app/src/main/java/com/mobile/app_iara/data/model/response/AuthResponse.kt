package com.mobile.app_iara.data.model.response

import com.google.gson.annotations.SerializedName

class AuthResponse (
    @SerializedName("access_token")
    val accessToken: String,

    @SerializedName("refresh_token")
    val refreshToken: String,

    @SerializedName("token_type")
    val tokenType: String
)