import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.WorkerParameters
import com.mobile.app_iara.AppDatabase
import com.mobile.app_iara.MainActivity
import com.mobile.app_iara.R
import com.mobile.app_iara.ui.notifications.NotificationEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.work.CoroutineWorker

const val KEY_NOTIFICATION_TITLE = "notification_title"
const val KEY_NOTIFICATION_DESC = "notification_description"
const val KEY_NOTIFICATION_LINK = "notification_link"

class NotificationWorker(private val appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val title = inputData.getString(KEY_NOTIFICATION_TITLE) ?: return Result.failure()
        val description = inputData.getString(KEY_NOTIFICATION_DESC) ?: return Result.failure()
        val link = inputData.getString(KEY_NOTIFICATION_LINK)

        showSystemNotification(title, description)
        saveNotificationToDatabase(title, description, link)

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

    private suspend fun saveNotificationToDatabase(title: String, description: String, link: String?) {
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