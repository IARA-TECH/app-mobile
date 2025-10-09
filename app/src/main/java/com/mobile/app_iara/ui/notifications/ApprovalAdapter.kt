package com.mobile.app_iara.ui.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobile.app_iara.R

class ApprovalAdapter (private val approvals: List<ApprovalModal>) :
    RecyclerView.Adapter<ApprovalAdapter.ApprovalsViewHolder>(){

    class ApprovalsViewHolder(notificationView: View) : RecyclerView.ViewHolder(notificationView) {
        val time: TextView = notificationView.findViewById(R.id.timeTxt)
        val link: TextView =  notificationView.findViewById(R.id.actionTxt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApprovalsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_confirmation_notification, parent, false)
        return ApprovalsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ApprovalsViewHolder, position: Int) {
        val approval = approvals[position]
        holder.time.text = approval.time
        holder.link.text = approval.link
    }

    override fun getItemCount() = approvals.size
}