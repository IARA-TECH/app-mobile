package com.mobile.app_iara.data.model.response

import com.google.gson.annotations.SerializedName
import com.mobile.app_iara.data.model.SheetData

data class SheetsResponse(
    val message: String,
    val status: Int,
    val timestamp: String,
    val data: List<SheetData>
)
