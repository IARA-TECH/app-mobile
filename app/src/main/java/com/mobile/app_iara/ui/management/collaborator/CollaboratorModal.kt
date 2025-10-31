package com.mobile.app_iara.ui.management.collaborator

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class CollaboratorModal(
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