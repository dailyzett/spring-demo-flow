package com.example.paymentdemo.service

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class NotificationService {
    private val _notifications = MutableSharedFlow<String>()
    val notifications: SharedFlow<String> = _notifications.asSharedFlow()

    private val log = LoggerFactory.getLogger(NotificationService::class.java)

    suspend fun sendNotification(message: String) {
        log.info("notification:{}", message)
        _notifications.emit(message)
    }
}