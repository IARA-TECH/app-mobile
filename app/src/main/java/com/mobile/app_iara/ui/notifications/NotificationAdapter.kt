package com.mobile.app_iara.ui.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobile.app_iara.R

class NotificationAdapter(
    private var notifications: List<NotificationEntity>,
    private val onLinkClicked: (NotificationEntity) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    class ViewHolder(view: View, val onLinkClicked: (NotificationEntity) -> Unit) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.titleNotification)
        val description: TextView = view.findViewById(R.id.descriptionNotification)
        val time: TextView = view.findViewById(R.id.timeTxt)
        val linkTextView: TextView = view.findViewById(R.id.actionTxt)

        fun bind(notification: NotificationEntity) {
            title.text = notification.title
            description.text = notification.description
            time.text = notification.time
            if (notification.link != null) {
                linkTextView.visibility = View.VISIBLE
                linkTextView.text = "Ver mais"

                linkTextView.setOnClickListener {
                    onLinkClicked(notification)
                }
            } else {
                linkTextView.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view, onLinkClicked)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(notifications[position])
    }

    override fun getItemCount() = notifications.size

    fun updateList(newList: List<NotificationEntity>) {
        notifications = newList
        notifyDataSetChanged()
    }
}