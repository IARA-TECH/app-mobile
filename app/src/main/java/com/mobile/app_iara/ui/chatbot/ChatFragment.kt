package com.mobile.app_iara.ui.chatbot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.app_iara.databinding.FragmentChatBinding
import com.mobile.app_iara.ui.chatbot.ChatAdapter
import com.mobile.app_iara.ui.chatbot.ChatMessage
import com.mobile.app_iara.ui.chatbot.Sender

class ChatFragment : Fragment() {

    // Padrão recomendado para View Binding em Fragments para evitar memory leaks.
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val messageList = mutableListOf<ChatMessage>()
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Infla o layout para este fragment
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Toda a lógica que manipula as views vai aqui.
        setupRecyclerView()
        loadInitialMessages()

        binding.btnSend.setOnClickListener {
            sendMessage()
        }
    }

    private fun setupRecyclerView() {
        // Usamos requireContext() para obter o contexto do fragment
        chatAdapter = ChatAdapter(messageList)
        binding.rvChatMessages.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = chatAdapter
        }
    }

    private fun loadInitialMessages() {
        messageList.add(ChatMessage("Olá, eu sou a Iara! No que posso te ajudar?", "09:26", Sender.BOT))
        messageList.add(ChatMessage("Como eu tiro foto do ábaco?", "09:27", Sender.USER))
        messageList.add(ChatMessage("Primeiro abra a home do app e clique no botão azul localizado no canto inferior direito, depois apenas siga o passo a passo.", "09:27", Sender.BOT))
        chatAdapter.notifyDataSetChanged()
    }

    private fun sendMessage() {
        val text = binding.etMessage.text.toString()
        if (text.isNotBlank()) {
            val userMessage = ChatMessage(text, "10:00", Sender.USER)
            messageList.add(userMessage)
            binding.etMessage.text.clear()

            chatAdapter.notifyItemInserted(messageList.size - 1)
            binding.rvChatMessages.scrollToPosition(messageList.size - 1)

            // Lógica para chamar a API e receber a resposta do bot...
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Limpa a referência do binding quando a view do fragment é destruída.
        _binding = null
    }
}