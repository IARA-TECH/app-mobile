package com.mobile.app_iara.data.remote

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    private val credentials = Credentials.basic("MarinaCostela@pinto.org", "123456")

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("Authorization", credentials)
            .build()

        return chain.proceed(request)
    }
}