package com.mobile.app_iara.data.model.response

import com.google.gson.annotations.SerializedName
import com.mobile.app_iara.data.model.AbacusData

data class AbacusListResponse(
    val message: String,
    val status: Int,
    val timestamp: String,
    val data: List<AbacusData>
)