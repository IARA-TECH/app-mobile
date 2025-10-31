package com.mobile.app_iara.data.remote

import com.mobile.app_iara.data.remote.service.AbacusPhotoService
import com.mobile.app_iara.data.remote.service.AbacusService
import com.mobile.app_iara.data.remote.service.FactoryService
import com.mobile.app_iara.data.remote.service.NewsService
import com.mobile.app_iara.data.remote.service.ShiftService
import com.mobile.app_iara.data.remote.service.UserService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val SQL_BASE_URL = "https://iara-api-sql.onrender.com/api/v1/"
    private const val MONGO_BASE_URL = "https://iara-api-mongo.onrender.com/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val authInterceptor = AuthInterceptor()

    private val sqlOkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(authInterceptor)
        .build()

    private val gsonConverterFactory = GsonConverterFactory.create()

    private val mongoOkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val sqlRetrofit = Retrofit.Builder()
        .baseUrl(SQL_BASE_URL)
        .client(sqlOkHttpClient)
        .addConverterFactory(gsonConverterFactory)
        .build()

    private val mongoRetrofit = Retrofit.Builder()
        .baseUrl(MONGO_BASE_URL)
        .client(mongoOkHttpClient)
        .addConverterFactory(gsonConverterFactory)
        .build()

    val userService: UserService by lazy {
        sqlRetrofit.create(UserService::class.java)
    }

    val factoryService: FactoryService by lazy {
        sqlRetrofit.create(FactoryService::class.java)
    }

    val abacusService: AbacusService by lazy {
        mongoRetrofit.create(AbacusService::class.java)
    }

    val abacusPhotosService: AbacusPhotoService by lazy {
        mongoRetrofit.create(AbacusPhotoService::class.java)
    }

    val newsService: NewsService by lazy {
        mongoRetrofit.create(NewsService::class.java)
    }
}