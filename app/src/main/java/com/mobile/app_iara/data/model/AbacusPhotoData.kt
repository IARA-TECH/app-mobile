package com.mobile.app_iara.data.model

data class AbacusPhotoData(
    val id: String,
    val factoryId: Int,
    val shiftId: String,
    val abacus: AbacusInfo,
    val takenBy: String?,
    val takenAt: String,
    val photoUrlBlob: String,
    val sheetUrlBlob: String,
    val validatedBy: String?,
    val shiftName: String,
    val shiftStartsAt: String,
    val shiftEndsAt: String
)

data class AbacusInfo(
    val id: String,
    val name: String,
    val description: String
)