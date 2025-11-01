package com.mobile.app_iara.data.model.response

import com.mobile.app_iara.data.model.AbacusPhotoData

data class AbacusPhotoResponse(
    val message: String,
    val status: Int,
    val data: List<AbacusPhotoData>
)
