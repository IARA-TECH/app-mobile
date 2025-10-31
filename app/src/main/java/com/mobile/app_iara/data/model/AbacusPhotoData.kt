package com.mobile.app_iara.data.model

data class AbacusPhotoData(
    val id: String,
    val factoryId: Int,
    val shiftId: String,
    val abacusId: String,
    val takenBy: String,
    val takenAt: String,
    val date: String,
    val urlBlob: String,
    val validatedBy: String?,
    val shiftName: String
)
