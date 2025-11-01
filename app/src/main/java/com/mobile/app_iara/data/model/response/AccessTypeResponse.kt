package com.mobile.app_iara.data.model.response

import com.google.gson.annotations.SerializedName

class AccessTypeResponse (
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String
)