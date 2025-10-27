package com.mobile.app_iara.ui.notifications

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.app_iara.R
import com.mobile.app_iara.ui.error.WifiErrorActivity
import com.mobile.app_iara.util.NetworkUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationsFragment : Fragment(R.layout.fragment_notifications) {

    private val viewModel: NotificationsViewModel by viewModels()
    private lateinit var adapterNotifications: NotificationAdapter
    private lateinit var adapterApprovals: ApprovalAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!NetworkUtils.isInternetAvailable(requireContext())) {
            val intent = Intent(requireContext(), WifiErrorActivity::class.java)
            startActivity(intent)
            activity?.finish()
            return
        }

        val recyclerNotifications: RecyclerView = view.findViewById(R.id.recyclerNotifications)
        val recyclerApprovals: RecyclerView = view.findViewById(R.id.recyclerApprovals)
        val emptyApprovals: TextView = view.findViewById(R.id.emptyApprovals)
        val emptyScreen: LinearLayout = view.findViewById(R.id.emptyScreen)
        val btnVoltar: ImageButton = view.findViewById(R.id.btnVoltar101)

        btnVoltar.setOnClickListener {
            findNavController().popBackStack()
        }

        adapterApprovals = ApprovalAdapter(emptyList())
        recyclerApprovals.adapter = adapterApprovals
        recyclerApprovals.layoutManager = LinearLayoutManager(requireContext())

        adapterNotifications = NotificationAdapter(emptyList())
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
            checkEmptyState()
        })

        viewModel.pendingApprovals.observe(viewLifecycleOwner, Observer { approvalList ->
            if (approvalList.isNullOrEmpty()) {
                recyclerApprovals.visibility = View.GONE
                emptyApprovals.visibility = View.VISIBLE
            } else {
                recyclerApprovals.visibility = View.VISIBLE
                emptyApprovals.visibility = View.GONE
                adapterApprovals.updateList(approvalList)
            }
            checkEmptyState()
        })
    }

    private fun checkEmptyState() {
        val approvalsEmpty = viewModel.pendingApprovals.value.isNullOrEmpty()
        val notificationsEmpty = viewModel.notifications.value.isNullOrEmpty()

        val emptyScreen: LinearLayout? = view?.findViewById(R.id.emptyScreen)

        if (approvalsEmpty && notificationsEmpty) {
            emptyScreen?.visibility = View.VISIBLE
        } else {
            emptyScreen?.visibility = View.GONE
        }
    }

    private fun createNotification(title: String, description: String, link: String) {
        val notification = NotificationEntity(
            title = title,
            description = description,
            time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        )
        viewModel.addNotification(notification)
    }
}