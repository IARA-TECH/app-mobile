package com.mobile.app_iara.ui.notifications.worker

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.WorkerParameters
import com.mobile.app_iara.data.local.AppDatabase
import com.mobile.app_iara.ui.MainActivity
import com.mobile.app_iara.R
import com.mobile.app_iara.ui.notifications.data.NotificationEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.work.CoroutineWorker

class NotificationWorker(private val appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val title = inputData.getString(KEY_NOTIFICATION_TITLE) ?: return Result.failure()
        val description = inputData.getString(KEY_NOTIFICATION_DESC) ?: return Result.failure()

        showSystemNotification(title, description)
        saveNotificationToDatabase(title, description)

        return Result.success()
    }

    private fun showSystemNotification(title: String, description: String) {
        val intent = Intent(appContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            appContext, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(appContext, "DAILY_REMINDER_CHANNEL_ID")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(appContext)) {
            try {
                notify(101, builder.build())
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun saveNotificationToDatabase(title: String, description: String) {
        val dao = AppDatabase.getDatabase(appContext).notificationDAO()

        val now = Date()

        val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentTimeString = timeFormatter.format(now)

        val currentTimestamp = now.time

        val notificationItem = NotificationEntity(
            title = title,
            description = description,
            time = currentTimeString,
            timestamp = currentTimestamp
        )
        dao.insert(notificationItem)
    }
}