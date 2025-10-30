package com.mobile.app_iara.data.model.response

import com.google.gson.annotations.SerializedName

data class MessageResponse(
    @SerializedName("response")
    val response: MessageData
)

data class MessageData(
    @SerializedName("response")
    val response: String
)