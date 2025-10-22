package com.mobile.app_iara.data.model.response

import com.google.gson.annotations.SerializedName

class UserAccessTypeResponse (
    @SerializedName("userId")
    val userId: String,

    @SerializedName("accessTypeId")
    val accessTypeId: Int,

    @SerializedName("accessTypeName")
    val accessTypeName: String
)