package com.mobile.app_iara.data.model.request

class RegisterCollaboratorRequest (
    val name: String,
    val email: String,
    val password: String,
    val dateOfBirth: String,
    val user_manager_id: String,
    val factory_id: Int,
    val gender_id: Int
)