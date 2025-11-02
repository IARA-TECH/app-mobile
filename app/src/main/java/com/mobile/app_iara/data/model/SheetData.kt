package com.mobile.app_iara.data.model

import com.google.gson.annotations.SerializedName

data class SheetData(
    @SerializedName("id")
    val id: String,

    @SerializedName("factoryId")
    val factoryId: Int,

    @SerializedName("abacusPhotoIds")
    val abacusPhotoIds: List<String>,

    @SerializedName("date")
    val date: String,

    @SerializedName("sheetUrlBlob")
    val sheetUrlBlob: String,

    @SerializedName("shiftId")
    val shiftId: String,

    @SerializedName("shiftName")
    val shiftName: String,

    @SerializedName("shiftStartsAt")
    val shiftStartsAt: String,

    @SerializedName("shiftEndsAt")
    val shiftEndsAt: String
)