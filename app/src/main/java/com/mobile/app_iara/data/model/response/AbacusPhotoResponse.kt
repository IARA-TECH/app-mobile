package com.mobile.app_iara.data.model.response

import com.mobile.app_iara.data.model.AbacusData
import com.mobile.app_iara.data.model.AbacusPhotoData

data class AbacusPhotosApiResponse(
    val message: String,
    val status: Int,
    val data: List<AbacusPhotoData>
)
