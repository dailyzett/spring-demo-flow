package com.example.paymentdemo.controller

import com.example.paymentdemo.domain.TestEvent
import com.example.paymentdemo.service.EventBus
import com.example.paymentdemo.service.NotificationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.ConcurrentHashMap

@RestController
@RequestMapping("/shared")
class SharedFlowController(
    private val eventBus: EventBus,
    private val notificationsService: NotificationService,
) {
    private val emitters = ConcurrentHashMap<String, SseEmitter>()

    @PostMapping
    suspend fun mutableSharedFlow(@RequestBody event: TestEvent) = coroutineScope {
        eventBus.produceEvent(event)
    }

    @PostMapping("/send")
    suspend fun sendNotification(@RequestBody message: String) {
        notificationsService.sendNotification(message)
    }

    @GetMapping("/subscribe", produces = ["text/event-stream"])
    fun subscribeNotifications(): SseEmitter {
        val emitter = SseEmitter(Long.MAX_VALUE)
        val emitterId = System.currentTimeMillis().toString()
        emitters[emitterId] = emitter

        emitter.onCompletion {
            emitters.remove(emitterId)
        }

        emitter.onTimeout {
            emitters.remove(emitterId)
            emitter.complete()
        }

        CoroutineScope(Dispatchers.IO).launch {
            notificationsService.notifications.collect { notifications ->
                try {
                    emitter.send(SseEmitter.event().data(notifications))
                } catch (e: Exception) {
                    emitters.remove(emitterId)
                    emitter.completeWithError(e)
                }
            }
        }

        return emitter
    }
}