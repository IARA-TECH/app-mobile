package com.mobile.app_iara.ui.notifications

import androidx.lifecycle.LiveData

class NotificationRepository(private val notificationDAO: NotificationDAO) {

    val allNotifications: LiveData<List<NotificationEntity>> = notificationDAO.getAllNotifications()

    suspend fun saveNotification(notification: NotificationEntity) {
        notificationDAO.insert(notification)
    }

    suspend fun clearAll() {
        notificationDAO.clearAll()
    }
}