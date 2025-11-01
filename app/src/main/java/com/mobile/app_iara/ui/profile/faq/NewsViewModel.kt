package com.mobile.app_iara.ui.profile.faq
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.app_iara.data.model.NewsData
import com.mobile.app_iara.data.repository.NewsRepository
import kotlinx.coroutines.launch

class NewsViewModel(
    private val repository: NewsRepository = NewsRepository()
) : ViewModel() {

    private val _news = MutableLiveData<List<NewsData>>()
    val news: LiveData<List<NewsData>> = _news

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchNews(keywords: List<String>? = null) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getNews(keywords)
                _news.postValue(response.data)
            } catch (e: Exception) {
                _error.postValue("Falha ao carregar notícias: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}