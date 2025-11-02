package com.mobile.app_iara.data.model

data class SheetData(
    val id: String,
    val factoryId: Int,
    val abacusPhotoIds: List<String>,
    val date: String,
    val sheetUrlBlob: String,
    val shiftId: String,
    val shiftName: String,
    val shiftStartsAt: String,
    val shiftEndsAt: String
)