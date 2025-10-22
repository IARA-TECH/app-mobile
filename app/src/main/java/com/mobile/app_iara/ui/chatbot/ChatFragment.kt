package com.mobile.app_iara.ui.chatbot

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.app_iara.databinding.FragmentChatBinding
import com.mobile.app_iara.ui.error.WifiErrorActivity
import com.mobile.app_iara.utils.NetworkUtils
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val messageList = mutableListOf<ChatMessage>()
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!NetworkUtils.isInternetAvailable(requireContext())) {
            val intent = Intent(requireContext(), WifiErrorActivity::class.java)
            startActivity(intent)
            activity?.finish()
            return
        }

        setupRecyclerView()
        loadInitialMessages()
        binding.btnSend.setOnClickListener {
            sendMessage()
        }
        binding.included.imgBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
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
            val currentTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            val formattedTime = currentTime.format(formatter)
            val userMessage = ChatMessage(text, formattedTime, Sender.USER)

            messageList.add(userMessage)
            binding.etMessage.text.clear()

            chatAdapter.notifyItemInserted(messageList.size - 1)
            binding.rvChatMessages.scrollToPosition(messageList.size - 1)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}