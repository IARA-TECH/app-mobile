package com.mobile.app_iara.data.model.response

import com.google.gson.annotations.SerializedName

data class UserProfileResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("dateOfBirth")
    val dateOfBirth: String,

    @SerializedName("userManagerName")
    val userManagerName: String,

    @SerializedName("userManagerId")
    val userManagerId: String,

    @SerializedName("genderName")
    val genderName: String,

    @SerializedName("genderId")
    val genderId: Int,

    @SerializedName("factoryName")
    val factoryName: String,

    @SerializedName("factoryId")
    val factoryId: Int
)