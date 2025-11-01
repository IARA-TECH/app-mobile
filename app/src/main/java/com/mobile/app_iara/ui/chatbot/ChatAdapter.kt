package com.mobile.app_iara.ui.chatbot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobile.app_iara.R

private const val VIEW_TYPE_USER = 1
private const val VIEW_TYPE_BOT = 2
private const val VIEW_TYPE_TYPING = 3

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

    class TypingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val dot1: View = view.findViewById(R.id.dot1)
        private val dot2: View = view.findViewById(R.id.dot2)
        private val dot3: View = view.findViewById(R.id.dot3)

        fun bind() {
            dot1.startAnimation(
                android.view.animation.AnimationUtils.loadAnimation(
                    itemView.context,
                    R.anim.typing_animation
                ).apply { startOffset = 0 }
            )

            dot2.startAnimation(
                android.view.animation.AnimationUtils.loadAnimation(
                    itemView.context,
                    R.anim.typing_animation
                ).apply { startOffset = 200 }
            )

            dot3.startAnimation(
                android.view.animation.AnimationUtils.loadAnimation(
                    itemView.context,
                    R.anim.typing_animation
                ).apply { startOffset = 400 }
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (messages[position].sender) {
            Sender.USER -> VIEW_TYPE_USER
            Sender.BOT -> VIEW_TYPE_BOT
            Sender.TYPING -> VIEW_TYPE_TYPING
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_USER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_user, parent, false)
                UserMessageViewHolder(view)
            }
            VIEW_TYPE_BOT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_bot, parent, false)
                BotMessageViewHolder(view)
            }
            VIEW_TYPE_TYPING -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_typing, parent, false)
                TypingViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
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
            VIEW_TYPE_TYPING -> {
                (holder as TypingViewHolder).bind()
            }
        }
    }

    override fun getItemCount() = messages.size
}