package com.mobile.app_iara.ui.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobile.app_iara.R
import com.mobile.app_iara.data.model.AbacusPhotoData // Importe o modelo correto

class ApprovalAdapter(
    private var approvals: List<AbacusPhotoData>,
    private val onItemClick: (AbacusPhotoData) -> Unit
) : RecyclerView.Adapter<ApprovalAdapter.ApprovalsViewHolder>() {

    class ApprovalsViewHolder(notificationView: View) : RecyclerView.ViewHolder(notificationView) {
        val time: TextView = notificationView.findViewById(R.id.timeTxt)
        val title: TextView = notificationView.findViewById(R.id.titleConfirmation)
        val description: TextView = notificationView.findViewById(R.id.txtDescription)
        val actionText: TextView = notificationView.findViewById(R.id.actionTxt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApprovalsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_confirmation_notification, parent, false)
        return ApprovalsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ApprovalsViewHolder, position: Int) {
        val approval = approvals[position]

        holder.title.text = "Solicitação de aprovação"
        holder.description.text = "Aprovação pendente enviada por ${approval.takenBy}"
        holder.time.text = "Registrado em: ${approval.takenAt}"

        holder.itemView.setOnClickListener {
            onItemClick(approval)
        }
        holder.actionText.setOnClickListener {
            onItemClick(approval)
        }
    }

    override fun getItemCount() = approvals.size

    fun updateList(newList: List<AbacusPhotoData>) {
        approvals = newList
        notifyDataSetChanged()
    }
}