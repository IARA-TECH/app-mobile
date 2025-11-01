package com.mobile.app_iara.data.model.request

import com.google.gson.annotations.SerializedName

data class EmailRequest(
    @SerializedName("email")
    val email: String
)