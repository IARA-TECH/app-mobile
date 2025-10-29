package com.mobile.app_iara.ui.management.collaborator

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class CollaboratorModal(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val urlPhoto: String?
) : Parcelable