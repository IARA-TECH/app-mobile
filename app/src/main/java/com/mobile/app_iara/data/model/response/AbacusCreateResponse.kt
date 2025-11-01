package com.mobile.app_iara.data.model.response

import com.google.gson.annotations.SerializedName
import com.mobile.app_iara.data.model.AbacusData

data class AbacusCreateResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("status")
    val status: Int,

    @SerializedName("timestamp")
    val timestamp: String,

    @SerializedName("data")
    val data: AbacusData
)