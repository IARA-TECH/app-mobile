package com.mobile.app_iara.ui.notifications

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mobile.app_iara.data.local.AppDatabase
import com.mobile.app_iara.data.model.AbacusPhotoData
import com.mobile.app_iara.data.repository.AbacusPhotoRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NotificationRepository

    val notifications: LiveData<List<NotificationEntity>>

    private val abacusPhotoRepository: AbacusPhotoRepository

    private val _pendingApprovals = MutableLiveData<List<AbacusPhotoData>>()
    val pendingApprovals: LiveData<List<AbacusPhotoData>> = _pendingApprovals

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        val dao = AppDatabase.getDatabase(application).notificationDAO()
        repository = NotificationRepository(dao)
        notifications = repository.allNotifications
        abacusPhotoRepository = AbacusPhotoRepository()
    }

    fun fetchPendingApprovals(factoryId: Int) {
        viewModelScope.launch {
            val result = abacusPhotoRepository.getPendingPhotosByFactory(factoryId)

            result.onSuccess { photoList ->
                _pendingApprovals.postValue(photoList)
            }.onFailure { exception ->
                _error.postValue(exception.message)
            }
        }
    }

    fun addNotification(title: String, description: String) {
        viewModelScope.launch {
            val now = Date()
            val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

            val notification = NotificationEntity(
                title = title,
                description = description,
                time = timeFormatter.format(now),
                timestamp = now.time
            )
            repository.saveNotification(notification)
        }
    }
}