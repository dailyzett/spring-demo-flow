package com.example.paymentdemo.controller

import com.example.paymentdemo.domain.TestEvent
import com.example.paymentdemo.service.EventBus
import kotlinx.coroutines.coroutineScope
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/shared")
class SharedFlowController(
    private val eventBus: EventBus,
) {

    @PostMapping
    suspend fun mutableSharedFlow(@RequestBody event: TestEvent) = coroutineScope {
        eventBus.produceEvent(event)
    }
}