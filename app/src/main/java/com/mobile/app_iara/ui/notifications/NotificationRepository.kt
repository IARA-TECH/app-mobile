package com.mobile.app_iara.ui.notifications

import androidx.lifecycle.LiveData
import java.time.LocalDate
import java.time.ZoneId

class NotificationRepository(private val notificationDAO: NotificationDAO) {

    private fun getTodayStartTimestamp(): Long {
        return LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    val allNotifications: LiveData<List<NotificationEntity>> =
        notificationDAO.getTodaysNotifications(getTodayStartTimestamp())

    suspend fun saveNotification(notification: NotificationEntity) {
        notificationDAO.insert(notification)
    }

    suspend fun clearOldNotifications() {
        notificationDAO.clearOldNotifications(getTodayStartTimestamp())
    }
}