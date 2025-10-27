package com.mobile.app_iara.data.model.response

import com.mobile.app_iara.data.model.AbacusData

data class AbacusResponse(
    val message: String,
    val status: Int,
    val timestamp: String,
    val data: List<AbacusData>
)
