package com.mobile.app_iara.ui.notifications

class NotificationRepository(private val dao: NotificationDAO) {

    suspend fun getAllNotifications(): List<NotificationModal> {
        return dao.getAllNotifications().map {
            NotificationModal(
                title = it.title,
                description = it.description,
                time = it.time,
                link = it.link
            )
        }
    }

    suspend fun saveNotification(entity: NotificationEntity) {
        dao.insertNotification(entity)
    }
}