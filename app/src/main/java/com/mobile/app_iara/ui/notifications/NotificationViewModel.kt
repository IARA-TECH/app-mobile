package com.mobile.app_iara.ui.notifications

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.mobile.app_iara.AppDatabase
import kotlinx.coroutines.launch

class NotificationsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NotificationRepository

    val notifications: LiveData<List<NotificationEntity>>

    init {
        val dao = AppDatabase.getDatabase(application).notificationDAO()
        repository = NotificationRepository(dao)
        notifications = repository.allNotifications
    }

    fun addNotification(notification: NotificationEntity) {
        viewModelScope.launch {
            repository.saveNotification(notification)
        }
    }
}