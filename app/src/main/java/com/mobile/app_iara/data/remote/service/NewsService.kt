package com.mobile.app_iara.data.remote.service

import com.mobile.app_iara.data.model.response.NewsResponse
import retrofit2.Response
import retrofit2.http.GET

interface NewsService {

    @GET("news")
    suspend fun getNews(): Response<NewsResponse>
}