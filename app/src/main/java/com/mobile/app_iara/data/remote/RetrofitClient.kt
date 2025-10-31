package com.mobile.app_iara.data.remote

import com.mobile.app_iara.data.remote.service.AccessTypeService
import com.mobile.app_iara.data.remote.service.DailyActiveUsersService
import com.mobile.app_iara.data.remote.service.FactoryService
import com.mobile.app_iara.data.remote.service.GenderService
import com.mobile.app_iara.data.remote.service.UserAccessTypeService
import com.mobile.app_iara.data.remote.service.UserService
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://iara-api-sql.onrender.com/api/v1/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val authInterceptor = Interceptor { chain ->
        val email = UserCredentialsHolder.email
        val password = UserCredentialsHolder.password
        val originalRequest = chain.request()

        if (email == null || password == null) {
            println("[Authentication] Sem credenciais salvas")
            return@Interceptor chain.proceed(originalRequest)
        }

        val credentials = Credentials.basic(email, password)
        println("[Authentication] Credenciais BASIC: $credentials")

        val newRequest = originalRequest.newBuilder()
            .header("Authorization", credentials)
            .build()

        println("[Authentication] Header Authorization adicionado: ${newRequest.header("Authorization")}")

        return@Interceptor chain.proceed(newRequest)
    }


    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val userService: UserService by lazy {
        retrofit.create(UserService::class.java)
    }

    val factoryService: FactoryService by lazy {
        retrofit.create(FactoryService::class.java)
    }

    val userAccessTypeService: UserAccessTypeService by lazy {
        retrofit.create(UserAccessTypeService::class.java)
    }

    val genderService: GenderService by lazy {
        retrofit.create(GenderService::class.java)
    }

    val accessTypeService: AccessTypeService by lazy {
        retrofit.create(AccessTypeService::class.java)
    }

    val dailyActiveUsersService: DailyActiveUsersService by lazy {
        retrofit.create(DailyActiveUsersService::class.java)
    }

}
