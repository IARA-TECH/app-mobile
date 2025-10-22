package com.mobile.app_iara.data.model.response

import com.google.gson.annotations.SerializedName

class UserAcessTypeResponse (
    @SerializedName("accessTypeId")
    val accessTypeId: Int,

    @SerializedName("userId")
    val userId: String,

    @SerializedName("accessTypeName")
    val accessTypeName: String

)