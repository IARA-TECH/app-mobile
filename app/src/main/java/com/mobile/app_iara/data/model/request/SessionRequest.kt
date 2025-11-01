package com.mobile.app_iara.data.model.request

import com.google.gson.annotations.SerializedName

class SessionRequest (
    @SerializedName("session_id")
    val sessionId: String
)