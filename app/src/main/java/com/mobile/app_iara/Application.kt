package com.mobile.app_iara

import android.app.Application
import android.util.Log
import com.mobile.app_iara.data.remote.UserCredentialsHolder

class IaraApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val sharedPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val savedEmail = sharedPrefs.getString("email", null)
        val savedPassword = sharedPrefs.getString("password", null)

        if (savedEmail != null && savedPassword != null) {
            UserCredentialsHolder.setCredentials(savedEmail, savedPassword)
            Log.e("APP_INIT", "UserCredentialsHolder PREENCHIDO")
        } else {
            Log.e("APP_INIT", "Credenciais não encontradas")
        }
    }
}