package com.mobile.app_iara.ui.notifications

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.mobile.app_iara.AppDatabase
import com.mobile.app_iara.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class NotificationsFragment : Fragment() {

    private lateinit var db: AppDatabase
    private lateinit var repository: NotificationRepository
    private lateinit var adapterApprovals: NotificationAdapter
    private lateinit var adapterNotifications: NotificationAdapter

    private lateinit var recyclerApprovals: RecyclerView
    private lateinit var recyclerNotifications: RecyclerView

    private lateinit var emptyApprovals: TextView
    private lateinit var emptyNotifications: TextView
    private lateinit var emptyScreen: LinearLayout


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerApprovals = view.findViewById(R.id.recyclerApprovals)
        recyclerNotifications = view.findViewById(R.id.recyclerNotifications)
        emptyApprovals = view.findViewById(R.id.emptyApprovals)
        emptyNotifications = view.findViewById(R.id.emptyNotifications)
        emptyScreen = view.findViewById(R.id.emptyScreen)

        adapterApprovals = NotificationAdapter(listOf())
        adapterNotifications = NotificationAdapter(listOf())

        recyclerApprovals.adapter = adapterApprovals
        recyclerNotifications.adapter = adapterNotifications

        loadNotifications()
    }

    private fun loadNotifications() {
        CoroutineScope(Dispatchers.IO).launch {
            val allNotifications = db.notificationDAO().getAllNotifications()

            withContext(Dispatchers.Main) {
                if (allNotifications.isEmpty()) {
                    emptyScreen.visibility = View.VISIBLE
                    emptyNotifications.visibility = View.GONE

                    recyclerNotifications.visibility = View.GONE
                    recyclerApprovals.visibility = View.GONE
                } else {
                    emptyScreen.visibility = View.GONE
                    emptyNotifications.visibility = View.GONE

                    recyclerNotifications.visibility = View.VISIBLE
                    recyclerApprovals.visibility = View.VISIBLE

                    adapterNotifications.updateList(allNotifications)
                }
            }
        }
    }

    private fun createNotification(title: String, description: String) {
        val notification = NotificationEntity(
            title = title,
            description = description,
            link = "",
            time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        )

        CoroutineScope(Dispatchers.IO).launch {
            repository.saveNotification(notification)
            withContext(Dispatchers.Main) {
                loadNotifications()
            }
        }
    }

    private fun scheduleDailyNotification(title: String, description: String) {
        val currentTime = Calendar.getInstance()
        val scheduledTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 18)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        if (scheduledTime.before(currentTime)) {
            scheduledTime.add(Calendar.DAY_OF_MONTH, 1)
        }

        val initialDelay = scheduledTime.timeInMillis - currentTime.timeInMillis

        val data = workDataOf(
            "title" to title,
            "description" to description,
            "link" to ""
        )

    }

    private fun saveNotification(title: String, description: String) {
        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val notification = NotificationEntity(
            title = title,
            description = description,
            link = "",
            time = time
        )
        CoroutineScope(Dispatchers.IO).launch {
            db.notificationDAO().insert(notification)
        }
    }
}