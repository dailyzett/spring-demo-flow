package com.example.paymentdemo.service

import com.example.paymentdemo.command.PaymentCommand
import com.example.paymentdemo.domain.PaymentEvent
import com.example.paymentdemo.domain.enum.EnumEventType
import com.example.paymentdemo.domain.jpo.EventJpo
import com.example.paymentdemo.domain.jpo.EventKey
import com.example.paymentdemo.domain.jpo.EventsJpoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.LocalDateTime

@Service
class PaymentService(
    private val eventsJpoRepository: EventsJpoRepository,
    private val clock: Clock,
    private val userService: UserService,
) {

    private val log = getLogger(this.javaClass)
    private val mutex = Mutex()

    @Transactional
    fun createOrder(command: PaymentCommand): Flow<PaymentEvent> = flow {
        mutex.withLock {
            val currentTime = LocalDateTime.now(clock)
            val key = EventKey(currentTime, EnumEventType.PAYMENT, command.username)
            emit(key to command.amount)
        }
    }.transformWhile { (key, amount) ->
            val paymentEventCount = eventsJpoRepository.countById(key)
            if (paymentEventCount > 0) {
                log.info("중복 이벤트 발생")
                false
            } else {
                emit(key to amount)
                true
            }
        }
        .onEach { (key, amount) -> eventsJpoRepository.save(EventJpo(key, amount)) }
        .map { (_, amount) -> PaymentEvent(command.username, amount) }
        .onEach { userService.subtractUserWallet(it) }
        .catch { e ->
            log.error("Error occurred while processing payment: ${e.message}", e)
            throw e
        }
        .flowOn(Dispatchers.IO)

}