package com.mobile.app_iara.data.remote

object UserCredentialsHolder {
    var email: String? = null
    var password: String? = null

    fun setCredentials(email: String?, pass: String?) {
        this.email = email
        this.password = pass
    }

    fun clear() {
        email = null
        password = null
    }
}
