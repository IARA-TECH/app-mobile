package com.mobile.app_iara.ui.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mobile.app_iara.AppDatabase
import com.mobile.app_iara.ui.notifications.NotificationRepository

class ClearNotificationsWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val database = AppDatabase.getDatabase(applicationContext)
            val repository = NotificationRepository(database.notificationDAO())

            repository.clearOldNotifications()

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}