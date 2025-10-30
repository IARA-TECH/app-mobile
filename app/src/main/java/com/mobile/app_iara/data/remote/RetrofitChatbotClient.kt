package com.mobile.app_iara.data.remote

import android.content.Context
import com.mobile.app_iara.data.remote.service.ChabotService
import com.mobile.app_iara.util.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitChatbotClient {

    private const val BASE_URL = "https://iara-api-chatbot.onrender.com/"
    private lateinit var sessionManager: SessionManager

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    fun init(context: Context) {
        sessionManager = SessionManager(context.applicationContext)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .authenticator(TokenAuthenticator(sessionManager, BASE_URL))
        .addInterceptor(AuthInterceptor(sessionManager))
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val chabotService: ChabotService by lazy {
        retrofit.create(ChabotService::class.java)
    }
}