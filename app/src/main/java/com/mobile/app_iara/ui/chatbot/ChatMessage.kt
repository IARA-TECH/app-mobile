package com.mobile.app_iara.ui.chatbot

data class ChatMessage(
    val text: String,
    val timestamp: String,
    val sender: Sender
)

enum class Sender {
    USER, BOT
}