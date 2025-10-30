package com.mobile.app_iara.data.model.request

import com.google.gson.annotations.SerializedName

class MessageRequest (
    @SerializedName("session_id")
    val sessionId: String,

    @SerializedName("user_message")
    val message: String
)