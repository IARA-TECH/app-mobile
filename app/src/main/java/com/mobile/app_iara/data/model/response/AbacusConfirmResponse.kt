package com.mobile.app_iara.data.model.response

import com.google.gson.annotations.SerializedName

data class AbacusConfirmResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("data")
    val data: AbacusConfirmData?
)

data class AbacusConfirmData(
    @SerializedName("photoUrl")
    val photoUrl: String,
    @SerializedName("sheetUrl")
    val sheetUrl: String
)