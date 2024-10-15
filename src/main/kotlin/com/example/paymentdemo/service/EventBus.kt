package com.example.paymentdemo.service

import com.example.paymentdemo.domain.Event
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class EventBus {
    private val log = LoggerFactory.getLogger(javaClass)
    private val _events = MutableSharedFlow<Event>()
    val events = _events.asSharedFlow()

    suspend fun produceEvent(event: Event) {
        log.info("Event produced: $event")
        _events.emit(event)
    }
}