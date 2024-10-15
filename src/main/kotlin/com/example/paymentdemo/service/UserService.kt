package com.example.paymentdemo.service

import com.example.paymentdemo.domain.PaymentEvent
import com.example.paymentdemo.domain.jpo.UserJpoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory.getLogger
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userJpoRepository: UserJpoRepository,
    private val eventBus: EventBus,
) {

    private val log = getLogger(this.javaClass)
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        subscribeEventBus()
    }

    @Transactional
    suspend fun subtractUserWallet(event: PaymentEvent) = withContext(Dispatchers.IO) {
        val foundUser = userJpoRepository.findByIdOrNull(event.userEmail)
        log.info("찾은 사용자:{}", foundUser)

        foundUser?.currentBalance = foundUser!!.currentBalance - event.amount
        userJpoRepository.save(foundUser)
    }

    private fun subscribeEventBus() {
        scope.launch {
            log.info("Subscribing to EventBus")
            eventBus.events.collect { event ->
                log.info("EventBus: $event")
            }
        }
    }
}