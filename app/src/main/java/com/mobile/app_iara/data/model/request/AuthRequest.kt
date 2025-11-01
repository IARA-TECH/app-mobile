package com.mobile.app_iara.data.model.request

import com.google.gson.annotations.SerializedName

class AuthRequest (
    @SerializedName("email")
    var email: String,

    @SerializedName("password")
    var password: String
)