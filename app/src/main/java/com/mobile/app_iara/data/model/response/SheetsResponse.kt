package com.mobile.app_iara.data.model.response

import com.google.gson.annotations.SerializedName
import com.mobile.app_iara.data.model.SheetData

data class SheetsResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("status")
    val status: Int,

    @SerializedName("timestamp")
    val timestamp: String,

    @SerializedName("data")
    val data: List<SheetData>
)
