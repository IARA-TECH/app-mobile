package com.mobile.app_iara.ui.management.collaborator

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import java.sql.Date

@Parcelize
data class CollaboratorModal(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val gender: String,
    val dateBirth: String,
    val urlPhoto: String?
) : Parcelable