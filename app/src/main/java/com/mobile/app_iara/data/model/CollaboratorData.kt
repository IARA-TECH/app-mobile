package com.mobile.app_iara.data.model

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class CollaboratorData(
    val id: String,
    val name: String,
    val email: String,
    val dateBirth: String,
    val urlPhoto: String?,
    val genderName: String,
    val roleName: String?,
    val genderId: Int,
    val userManagerId: String?,
    val factoryId: Int
) : Parcelable