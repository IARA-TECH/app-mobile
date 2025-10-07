package com.mobile.app_iara.ui.notifications

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mobile.app_iara.AppDatabase
import kotlinx.coroutines.launch

class NotificationsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NotificationRepository

    private val _notifications = MutableLiveData<List<NotificationModal>>()
    val notifications: LiveData<List<NotificationModal>> = _notifications

    init {
        val dao = AppDatabase.getDatabase(application).notificationDAO()
        repository = NotificationRepository(dao)
        viewModelScope.launch {
            _notifications.value = repository.getAllNotifications()
        }
    }

    fun addNotification(notification: NotificationEntity) {
        viewModelScope.launch {
            repository.saveNotification(notification)
            _notifications.value = repository.getAllNotifications()
        }
    }
}
