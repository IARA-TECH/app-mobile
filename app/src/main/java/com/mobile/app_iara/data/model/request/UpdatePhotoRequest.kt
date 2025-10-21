package com.mobile.app_iara.data.model.request

import com.google.gson.annotations.SerializedName


data class UpdatePhotoRequest(
    @SerializedName("url_blob")
    val urlBlob: String,

    @SerializedName("user_id")
    val userId: String
)