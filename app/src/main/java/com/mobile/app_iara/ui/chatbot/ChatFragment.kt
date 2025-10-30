package com.mobile.app_iara.ui.chatbot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.app_iara.databinding.FragmentChatBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val messageList = mutableListOf<ChatMessage>()
    private lateinit var chatAdapter: ChatAdapter
    private val viewModel: ChatbotViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()

        binding.btnSend.setOnClickListener {
            val text = binding.etMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                addMessage(text, Sender.USER)
                viewModel.sendMessage(text)
                binding.etMessage.text.clear()
            }
        }
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(messageList)
        binding.rvChatMessages.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = chatAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.isReady.observe(viewLifecycleOwner) { ready ->
            if (ready == true) {
                addMessage("Olá, eu sou a Iara! Como posso te ajudar?", Sender.BOT)
            } else {
                addMessage("❌ Falha ao conectar com o servidor do chatbot.", Sender.BOT)
            }
        }

        viewModel.botMessage.observe(viewLifecycleOwner) { message ->
            addMessage(message, Sender.BOT)
        }
    }

    private fun addMessage(text: String, sender: Sender) {
        val currentTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val formattedTime = currentTime.format(formatter)

        messageList.add(ChatMessage(text, formattedTime, sender))
        chatAdapter.notifyItemInserted(messageList.size - 1)
        binding.rvChatMessages.scrollToPosition(messageList.size - 1)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
