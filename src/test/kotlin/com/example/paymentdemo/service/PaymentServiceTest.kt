package com.example.paymentdemo.service

import com.example.paymentdemo.command.PaymentCommand
import com.example.paymentdemo.domain.enum.EnumEventType
import com.example.paymentdemo.domain.jpo.EventKey
import com.example.paymentdemo.domain.jpo.EventsJpoRepository
import com.example.paymentdemo.domain.jpo.UserJpoRepository
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class PaymentServiceITest {

    @Autowired
    lateinit var eventsJpoRepository: EventsJpoRepository

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var userJpoRepository: UserJpoRepository

    private final val fixedInstant: Instant = Instant.parse("2024-10-14T15:00:00Z")
    val clock: Clock = Clock.fixed(fixedInstant, java.time.ZoneId.systemDefault())
    val command = PaymentCommand(username = "navicoin", amount = 500)

    lateinit var key: EventKey

    @BeforeEach
    fun setUp() {
        key = EventKey(LocalDateTime.now(clock), EnumEventType.PAYMENT, command.username)

        eventsJpoRepository.deleteById(key)

        val foundUser = userJpoRepository.findByIdOrNull(command.username)
        foundUser?.currentBalance = 10_000
        userJpoRepository.save(foundUser!!)
    }


    @Test
    fun createOrderTest() {
        val paymentService = PaymentService(eventsJpoRepository, clock, userService)
        val userJpo = userJpoRepository.findByIdOrNull(command.username)
        val expectedBalance = userJpo!!.currentBalance - command.amount
        println("기대값: $expectedBalance, 초기 값: ${userJpo.currentBalance}")

        runBlocking {
            val jobs = List(3) {
                launch {
                    paymentService.createOrder(command).collect { result ->
                        if (result.userEmail.isNotEmpty()) println("중복 이벤트가 발생했습니다.")
                    }
                }
            }

            jobs.joinAll()
        }

        val afterUserJpo = userJpoRepository.findByIdOrNull(command.username)
        val eventJpo = eventsJpoRepository.findByIdOrNull(key)
        println("After Balance: ${afterUserJpo?.currentBalance}")

        assertTrue { eventJpo!!.amount == command.amount }
        afterUserJpo?.let { assertTrue { it.currentBalance == expectedBalance } }
    }
}