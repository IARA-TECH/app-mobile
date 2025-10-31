package com.mobile.app_iara.ui.notifications

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
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
import com.mobile.app_iara.data.model.AbacusPhotoData
import com.mobile.app_iara.ui.error.WifiErrorActivity
import com.mobile.app_iara.util.NetworkUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.content.Context
import android.widget.Toast
import com.mobile.app_iara.ui.start.LoginActivity

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

        adapterApprovals = ApprovalAdapter(emptyList()) { photo ->
            showApprovalDialog(photo)
        }
        recyclerApprovals.adapter = adapterApprovals
        recyclerApprovals.layoutManager = LinearLayoutManager(requireContext())

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

        val prefs = requireActivity().getSharedPreferences(
            "user_prefs",
            Context.MODE_PRIVATE
        )

        val factoryId = prefs.getInt("key_factory_id", -1)

        if (factoryId != -1) {
            viewModel.fetchPendingApprovals(factoryId)
        } else {
            Toast.makeText(requireContext(), "Erro: ID da fábrica não encontrado.", Toast.LENGTH_LONG).show()
        }

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

    private fun showApprovalDialog(photo: AbacusPhotoData) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_photo_confirmation)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnNegar: Button = dialog.findViewById(R.id.btnNegarDialog)
        val btnConfirmar: Button = dialog.findViewById(R.id.btnConfirmarDialog)
        val linkPlanilha: TextView = dialog.findViewById(R.id.avisoDialogPhoto)

        linkPlanilha.setOnClickListener {

        }

        btnNegar.setOnClickListener {
            dialog.dismiss()
        }

        btnConfirmar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
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
        viewModel.addNotification(title, description)
    }
}