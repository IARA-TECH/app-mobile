package com.mobile.app_iara.data.model.request

import com.google.gson.annotations.SerializedName

class UserAccessTypeRequest (


    @SerializedName("access_type_id")
    val accessTypeId: Int,

    @SerializedName("user_id")
    val userId: String
)