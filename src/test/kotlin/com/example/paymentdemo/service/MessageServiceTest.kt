package com.example.paymentdemo.service

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

suspend fun <T> Flow<T>.toListDuring(
    duration: Duration
): List<T> = coroutineScope {
    val result = mutableListOf<T>()
    val job = launch {
        this@toListDuring.collect(result::add)
    }
    delay(duration)
    job.cancel()
    return@coroutineScope result
}

class MessageServiceTest {
    @Test
    fun `should emit messages from user`() = runTest {
        val source = flow {
            emit(Message("0", "A"))
            emit(Message("1", "B"))
            emit(Message("0", "C"))
        }
        val service = MessageService(
            messageSource = source,
            scope = backgroundScope
        )

        val emittedMessages = service.observeMessages("0")
            .toListDuring(1.milliseconds)

        assertEquals(
            listOf(
                Message("0", "A"),
                Message("0", "C"),
            ), emittedMessages
        )
    }
}