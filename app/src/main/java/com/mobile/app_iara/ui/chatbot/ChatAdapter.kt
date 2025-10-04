package com.mobile.app_iara.ui.chatbot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobile.app_iara.R

private const val VIEW_TYPE_USER = 1
private const val VIEW_TYPE_BOT = 2

class ChatAdapter(private val messages: List<ChatMessage>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class UserMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageText: TextView = view.findViewById(R.id.tvMessage)
        val timestampText: TextView = view.findViewById(R.id.tvTimestamp)

        fun bind(message: ChatMessage) {
            messageText.text = message.text
            timestampText.text = message.timestamp
        }
    }


    class BotMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageText: TextView = view.findViewById(R.id.tvMessage)
        val timestampText: TextView = view.findViewById(R.id.tvTimestamp)

        fun bind(message: ChatMessage) {
            messageText.text = message.text
            timestampText.text = message.timestamp
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].sender == Sender.USER) {
            VIEW_TYPE_USER
        } else {
            VIEW_TYPE_BOT
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_USER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_user, parent, false)
            UserMessageViewHolder(view)
        }
        else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_bot, parent, false)
            BotMessageViewHolder(view)
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]

        when (holder.itemViewType) {
            VIEW_TYPE_USER -> {
                (holder as UserMessageViewHolder).bind(message)
            }
            VIEW_TYPE_BOT -> {
                (holder as BotMessageViewHolder).bind(message)
            }
        }
    }

    override fun getItemCount() = messages.size
}