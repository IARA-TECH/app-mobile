package com.mobile.app_iara.data.model.response

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("response")
    val response: AuthData
)

data class AuthData(
    @SerializedName("access_token")
    val accessToken: String,

    @SerializedName("refresh_token")
    val refreshToken: String,

    @SerializedName("token_type")
    val tokenType: String
)