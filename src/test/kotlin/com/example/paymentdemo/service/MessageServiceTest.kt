package com.example.paymentdemo.service

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MessageServiceTest {
    @Test
    fun `should emit messages from user`() = runTest {
        val source = flow {
            emit(Message("0", "A"))
            delay(1000)
            emit(Message("1", "B"))
            emit(Message("0", "C"))
        }
        val service = MessageService(
            messageSource = source,
            scope = backgroundScope
        )

        val emittedMessages = mutableListOf<Message>()
        service.observeMessages("0")
            .onEach { emittedMessages.add(it) }
            .launchIn(backgroundScope)
        delay(1)

        assertEquals(
            listOf(
                Message("0", "A"),
            ), emittedMessages
        )

        delay(1000)

        assertEquals(
            listOf(
                Message("0", "A"),
                Message("0", "C"),
            ), emittedMessages
        )
    }
}