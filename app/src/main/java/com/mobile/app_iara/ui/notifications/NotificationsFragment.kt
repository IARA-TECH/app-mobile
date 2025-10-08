package com.mobile.app_iara.ui.notifications

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.app_iara.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationsFragment : Fragment(R.layout.fragment_notifications) {

    private val viewModel: NotificationsViewModel by viewModels()
    private lateinit var adapterNotifications: NotificationAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerNotifications: RecyclerView = view.findViewById(R.id.recyclerNotifications)
        val emptyScreen: LinearLayout = view.findViewById(R.id.emptyScreen)

        adapterNotifications = NotificationAdapter(emptyList()) { notification ->
            if (notification.link != null) {
                handleNavigation(notification.link)
            }
        }

        recyclerNotifications.adapter = adapterNotifications
        recyclerNotifications.layoutManager = LinearLayoutManager(requireContext())

        viewModel.notifications.observe(viewLifecycleOwner, Observer { notificationList ->
            if (notificationList.isNullOrEmpty()) {
                emptyScreen.visibility = View.VISIBLE
                recyclerNotifications.visibility = View.GONE
            } else {
                emptyScreen.visibility = View.GONE
                recyclerNotifications.visibility = View.VISIBLE
                adapterNotifications.updateList(notificationList)
            }
        })
    }

    private fun handleNavigation(targetLink: String) {
        when (targetLink) {
            "ANALISES" -> {
                findNavController().navigate(R.id.action_notificationsFragment_to_dashboardFragment)
            }
            "PERFIL" -> {
                findNavController().navigate(R.id.action_notificationsFragment_to_perfilFragment)
            }
        }
    }

    private fun createNotification(title: String, description: String, link: String) {
        val notification = NotificationEntity(
            title = title,
            description = description,
            link = link,
            time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        )
        viewModel.addNotification(notification)
    }
}