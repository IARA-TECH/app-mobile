package com.mobile.app_iara.ui.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobile.app_iara.R

class NotificationAdapter(
    private var notifications: List<NotificationEntity>
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.titleNotification)
        val description: TextView = view.findViewById(R.id.descriptionNotification)
        val time: TextView = view.findViewById(R.id.timeTxt)
        val link: TextView = view.findViewById(R.id.actionTxt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val n = notifications[position]
        holder.title.text = n.title
        holder.description.text = n.description
        holder.time.text = n.time
        holder.link.text = n.link
    }

    override fun getItemCount() = notifications.size

    fun updateList(newList: List<NotificationEntity>) {
        notifications = newList
        notifyDataSetChanged()
    }
}