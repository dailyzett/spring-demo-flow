package com.example.paymentdemo.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.shareIn

class MessageService(
    private val messageSource: Flow<Message>,
    scope: CoroutineScope
) {
    private val source = messageSource
        .shareIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(),
        )

    fun observeMessages(fromUserId: String) = source
        .filter { it.fromUserId == fromUserId }
}

data class Message(
    val fromUserId: String,
    val text: String,
)