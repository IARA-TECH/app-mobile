import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.mobile.app_iara.AppDatabase
import com.mobile.app_iara.MainActivity
import com.mobile.app_iara.R
import com.mobile.app_iara.ui.notifications.NotificationEntity
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationWorker(private val appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val title = "Já registrou a contagem hoje?"
        val description = "Não se esqueça de registrar os dados do dia."

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

    private fun saveNotificationToDatabase(title: String, description: String) {
        runBlocking {
            val dao = AppDatabase.getDatabase(appContext).notificationDAO()

            val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
            val currentTimeString = timeFormatter.format(Date())

            val notificationItem = NotificationEntity(
                title = title,
                description = description,
                time = currentTimeString,
                link = "so pra nao ficar dando erro"
            )
            dao.insert(notificationItem)
        }
    }
}