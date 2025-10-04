package com.mobile.app_iara.ui.chatbot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobile.app_iara.R

// Definimos constantes para os tipos de view. Isso torna o código mais legível
// do que usar números mágicos como 0 e 1.
private const val VIEW_TYPE_USER = 1
private const val VIEW_TYPE_BOT = 2

/**
 * Adapter para a RecyclerView do chat.
 * @param messages A lista de mensagens a serem exibidas.
 */
class ChatAdapter(private val messages: List<ChatMessage>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * ViewHolder para as mensagens do USUÁRIO.
     * Ele armazena as referências para as views dentro do layout 'item_chat_user.xml'.
     */
    class UserMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageText: TextView = view.findViewById(R.id.tvMessage)
        val timestampText: TextView = view.findViewById(R.id.tvTimestamp)

        fun bind(message: ChatMessage) {
            messageText.text = message.text
            timestampText.text = message.timestamp
        }
    }

    /**
     * ViewHolder para as mensagens do BOT.
     * Ele armazena as referências para as views dentro do layout 'item_chat_bot.xml'.
     */
    class BotMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageText: TextView = view.findViewById(R.id.tvMessage)
        val timestampText: TextView = view.findViewById(R.id.tvTimestamp)

        fun bind(message: ChatMessage) {
            messageText.text = message.text
            timestampText.text = message.timestamp
        }
    }

    /**
     * Este método é chamado pela RecyclerView para decidir qual tipo de layout usar
     * para um item em uma determinada posição.
     */
    override fun getItemViewType(position: Int): Int {
        // Verifica o remetente da mensagem na posição atual
        return if (messages[position].sender == Sender.USER) {
            VIEW_TYPE_USER
        } else {
            VIEW_TYPE_BOT
        }
    }

    /**
     * Este método é chamado quando a RecyclerView precisa de um novo ViewHolder.
     * É aqui que inflamos o layout XML correto com base no 'viewType'.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // Se for uma mensagem de usuário, infla o layout do usuário.
        return if (viewType == VIEW_TYPE_USER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_user, parent, false)
            UserMessageViewHolder(view)
        }
        // Caso contrário (é do bot), infla o layout do bot.
        else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_bot, parent, false)
            BotMessageViewHolder(view)
        }
    }

    /**
     * Este método é chamado para exibir os dados em uma posição específica.
     * Ele pega os dados da mensagem e os "amarra" (bind) às views do ViewHolder.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]

        // Verificamos o tipo do ViewHolder para fazer o cast correto e chamar seu método 'bind'.
        when (holder.itemViewType) {
            VIEW_TYPE_USER -> {
                (holder as UserMessageViewHolder).bind(message)
            }
            VIEW_TYPE_BOT -> {
                (holder as BotMessageViewHolder).bind(message)
            }
        }
    }

    /**
     * Este método simplesmente retorna o número total de itens na lista.
     * A RecyclerView usa isso para saber quantos itens precisa desenhar.
     */
    override fun getItemCount() = messages.size
}