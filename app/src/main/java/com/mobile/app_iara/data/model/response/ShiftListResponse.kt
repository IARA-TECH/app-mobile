package com.mobile.app_iara.data.model.response

import com.mobile.app_iara.data.model.ShiftData

data class ShiftListResponse(
    val message: String,
    val status: Int,
    val timestamp: String,
    val data: List<ShiftData>
)