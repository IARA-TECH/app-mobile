package com.mobile.app_iara.data.model.response

import com.google.gson.annotations.SerializedName

data class SessionResponse(
    @SerializedName("response")
    val response: SessionData
)

data class SessionData(
    @SerializedName("session_id")
    val sessionId: String
)