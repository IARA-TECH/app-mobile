package com.mobile.app_iara.data.repository

import com.mobile.app_iara.data.model.response.NewsResponse
import com.mobile.app_iara.data.remote.RetrofitClient

class NewsRepository {

    private val newsService = RetrofitClient.newsService

    suspend fun getNews(keywords: List<String>? = null): NewsResponse {
        val response = newsService.getNews(keywords)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Resposta OK, mas o corpo está vazio.")
        } else {
            throw Exception("Falha na busca: ${response.code()} - ${response.message()}")
        }
    }
}