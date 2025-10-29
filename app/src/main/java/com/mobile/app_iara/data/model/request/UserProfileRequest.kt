package com.mobile.app_iara.data.model.request

import com.google.gson.annotations.SerializedName

class UserProfileRequest (
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("date_of_birth")
    val dateOfBirth: String,
    @SerializedName("user_manager_id")
    val userManagerId: String,
    @SerializedName("factory_id")
    val factoryId: Int,
    @SerializedName("gender_id")
    val genderId: Int
)