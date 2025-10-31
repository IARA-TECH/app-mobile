package com.mobile.app_iara.data.model

data class AbacusPhotoData(
    val id: String,
    val factoryId: Int,
    val shiftId: String,
    val abacusId: String,
    val takenBy: String,
    val takenAt: String,
    val date: String,
    val photoUrlBlob: String,
    val sheetUrlBlob: String,
    val validatedBy: String?,
    val shiftName: String,
    val shiftStartsAt: String,
    val shiftEndsAt: String
)
