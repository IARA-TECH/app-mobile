package com.mobile.app_iara.ui.notifications.ui

import android.app.Dialog
import android.app.DownloadManager
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
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import com.mobile.app_iara.ui.status.LoadingApiFragment

class NotificationsFragment : Fragment(R.layout.fragment_notifications) {

    private val viewModel: NotificationsViewModel by viewModels()
    private lateinit var adapterNotifications: NotificationAdapter
    private lateinit var adapterApprovals: ApprovalAdapter

    private var loadingContainer: View? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .add(R.id.loading_container, LoadingApiFragment.newInstance())
                .commit()
        }

        if (!NetworkUtils.isInternetAvailable(requireContext())) {
            val intent = Intent(requireContext(), WifiErrorActivity::class.java)
            startActivity(intent)
            activity?.finish()
            return
        }

        loadingContainer = view.findViewById(R.id.loading_container)

        val recyclerNotifications: RecyclerView = view.findViewById(R.id.recyclerNotifications)
        val recyclerApprovals: RecyclerView = view.findViewById(R.id.recyclerApprovals)
        val emptyApprovals: TextView = view.findViewById(R.id.emptyApprovals)
        val emptyScreen: LinearLayout = view.findViewById(R.id.emptyScreen)
        val btnVoltar: ImageButton = view.findViewById(R.id.btnVoltar101)
        val titleApprovals: TextView? = view.findViewById(R.id.textView43)

        btnVoltar.setOnClickListener {
            findNavController().popBackStack()
        }

        val prefs = requireActivity().getSharedPreferences(
            "user_prefs",
            Context.MODE_PRIVATE
        )

        val factoryId = prefs.getInt("key_factory_id", -1)
        val userRoles = prefs.getStringSet("key_user_roles", emptySet()) ?: emptySet()

        val hasApprovalPermission = userRoles.contains("Supervisor") || userRoles.contains("Administrador")

        loadingContainer?.visibility = View.VISIBLE

        if (hasApprovalPermission) {
            titleApprovals?.visibility = View.VISIBLE

            adapterApprovals = ApprovalAdapter(emptyList()) { photo ->
                showApprovalDialog(photo)
            }
            recyclerApprovals.adapter = adapterApprovals
            recyclerApprovals.layoutManager = LinearLayoutManager(requireContext())

            viewModel.userMap.observe(viewLifecycleOwner, Observer { userMap ->
                adapterApprovals.updateUserMap(userMap)
            })

            viewModel.isApprovalDataReady.observe(viewLifecycleOwner, Observer { isReady ->
                if (isReady) {
                    loadingContainer?.visibility = View.GONE
                }
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

            if (factoryId != -1) {
                viewModel.fetchUserMap(factoryId)
                viewModel.fetchPendingApprovals(factoryId)
            } else {
                loadingContainer?.visibility = View.GONE
                Toast.makeText(requireContext(), "Erro: ID da fábrica não encontrado.", Toast.LENGTH_LONG).show()
            }

        } else {
            titleApprovals?.visibility = View.GONE
            recyclerApprovals.visibility = View.GONE
            emptyApprovals.visibility = View.GONE
        }

        adapterNotifications = NotificationAdapter(emptyList())
        recyclerNotifications.adapter = adapterNotifications
        recyclerNotifications.layoutManager = LinearLayoutManager(requireContext())

        viewModel.notifications.observe(viewLifecycleOwner, Observer { notificationList ->
            if (!hasApprovalPermission) {
                loadingContainer?.visibility = View.GONE
            }

            if (notificationList.isNullOrEmpty()) {
                recyclerNotifications.visibility = View.GONE
            } else {
                recyclerNotifications.visibility = View.VISIBLE
                adapterNotifications.updateList(notificationList)
            }
            checkEmptyState()
        })
    }

    private fun showApprovalDialog(photo: AbacusPhotoData) {
        val prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val factoryId = prefs.getInt("key_factory_id", -1)
        val userName = prefs.getString("key_user_name", null)

        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_photo_confirmation)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnNegar: Button = dialog.findViewById(R.id.btnNegarDialog)
        val btnConfirmar: Button = dialog.findViewById(R.id.btnConfirmarDialog)
        val linkPlanilha: TextView = dialog.findViewById(R.id.avisoDialogPhoto)

        if (factoryId == -1 || userName == null) {
            Toast.makeText(requireContext(), "Erro: Informações do usuário não encontradas.", Toast.LENGTH_LONG).show()
            return
        }

        linkPlanilha.setOnClickListener {
            val spreadsheetUrl = (photo as? AbacusPhotoData)?.sheetUrlBlob

            if (spreadsheetUrl.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "URL da planilha não encontrada.", Toast.LENGTH_SHORT).show()
            } else {
                val spreadsheetTitle = extractSpreadsheetName(spreadsheetUrl)
                downloadFile(requireContext(), spreadsheetUrl, spreadsheetTitle)
            }
        }

        btnNegar.setOnClickListener {
            loadingContainer?.visibility = View.VISIBLE
            viewModel.denyPhoto(photo.id, factoryId)
            dialog.dismiss()
        }

        btnConfirmar.setOnClickListener {
            loadingContainer?.visibility = View.VISIBLE
            viewModel.approvePhoto(photo.id, userName, factoryId)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun extractSpreadsheetName(url: String?): String {
        if (url.isNullOrEmpty()) {
            return "Título indisponível"
        }

        val fullFileName = url.substringAfterLast('/')
        val title = fullFileName.substringBeforeLast('.')

        if (title.isBlank()) {
            return "Título indisponível"
        }
        return title
    }

    private fun downloadFile(context: Context, url: String, fileTitle: String) {
        if (url.isNullOrEmpty()) {
            Toast.makeText(context, "URL da planilha é inválida.", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val fileNameWithExtension = url.substringAfterLast('/')

            val request = DownloadManager.Request(Uri.parse(url))
                .setTitle(fileTitle)
                .setDescription("Baixando planilha...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileNameWithExtension)
                .setAllowedOverMetered(true)

            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)

            Toast.makeText(context, "Iniciando download...", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Erro ao tentar baixar: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkEmptyState() {
        val prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userRoles = prefs.getStringSet("key_user_roles", emptySet()) ?: emptySet()
        val hasApprovalPermission = userRoles.contains("Supervisor") || userRoles.contains("Administrador")

        val approvalsEmpty: Boolean
        if (hasApprovalPermission) {
            approvalsEmpty = viewModel.pendingApprovals.value.isNullOrEmpty()
        } else {
            approvalsEmpty = true
        }

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