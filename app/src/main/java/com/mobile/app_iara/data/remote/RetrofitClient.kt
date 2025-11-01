package com.mobile.app_iara.data.remote

import com.mobile.app_iara.data.remote.service.AbacusPhotoService
import com.mobile.app_iara.data.remote.service.AbacusService
import com.mobile.app_iara.data.remote.service.AccessTypeService
import com.mobile.app_iara.data.remote.service.ChabotService
import com.mobile.app_iara.data.remote.service.DailyActiveUsersService
import com.mobile.app_iara.data.remote.service.FactoryService
import com.mobile.app_iara.data.remote.service.GenderService
import com.mobile.app_iara.data.remote.service.NewsService
import com.mobile.app_iara.data.remote.service.SheetService
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

    private const val SQL_BASE_URL = "https://iara-api-sql.onrender.com/api/v1/"
    private const val MONGO_BASE_URL = "https://iara-api-mongo.onrender.com/"
    private const val CHATBOT_BASE_URL = "https://iara-api-chatbot.onrender.com/"
    private const val NEWS_BASE_URL = "https://iara-api-web-scraping.onrender.com/"

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

    private val sqlOkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val mongoOkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val chabotOkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val newsOkHttp = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(50, TimeUnit.SECONDS)
        .readTimeout(50, TimeUnit.SECONDS)
        .writeTimeout(50, TimeUnit.SECONDS)
        .build()

    private val sqlRetrofit = Retrofit.Builder()
        .baseUrl(SQL_BASE_URL)
        .client(sqlOkHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val mongoRetrofit = Retrofit.Builder()
        .baseUrl(MONGO_BASE_URL)
        .client(mongoOkHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val chatbotRetrofit = Retrofit.Builder()
        .baseUrl(CHATBOT_BASE_URL)
        .client(chabotOkHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val newsRetrofit = Retrofit.Builder()
        .baseUrl(NEWS_BASE_URL)
        .client(chabotOkHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val userService: UserService by lazy {
        sqlRetrofit.create(UserService::class.java)
    }

    val factoryService: FactoryService by lazy {
        sqlRetrofit.create(FactoryService::class.java)
    }

    val userAccessTypeService: UserAccessTypeService by lazy {
        sqlRetrofit.create(UserAccessTypeService::class.java)
    }

    val genderService: GenderService by lazy {
        sqlRetrofit.create(GenderService::class.java)
    }

    val accessTypeService: AccessTypeService by lazy {
        sqlRetrofit.create(AccessTypeService::class.java)
    }

    val dailyActiveUsersService: DailyActiveUsersService by lazy {
        sqlRetrofit.create(DailyActiveUsersService::class.java)
    }

    val abacusService: AbacusService by lazy {
        mongoRetrofit.create(AbacusService::class.java)
    }

    val abacusPhotoService: AbacusPhotoService by lazy {
        mongoRetrofit.create(AbacusPhotoService::class.java)
    }

    val chabotService: ChabotService by lazy {
        chatbotRetrofit.create(ChabotService::class.java)
    }

    val newsService: NewsService by lazy {
        newsRetrofit.create(NewsService::class.java)
    }

    val sheetService: SheetService by lazy {
        mongoRetrofit.create(SheetService::class.java)
    }
}
