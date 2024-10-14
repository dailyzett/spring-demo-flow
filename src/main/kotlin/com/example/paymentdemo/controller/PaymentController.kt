package com.example.paymentdemo.controller

import com.example.paymentdemo.command.PaymentCommand
import com.example.paymentdemo.service.PaymentService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/payments")
class PaymentController(
    private val paymentService: PaymentService,
) {

    @PostMapping("")
    suspend fun createPayment(@RequestBody command: PaymentCommand): Flow<ResponseEntity<Void>> {
        val orderFlow = withContext(Dispatchers.IO) {
            paymentService.createOrder(command)
        }

        return orderFlow.map { result ->
            if(result.userEmail.isNotEmpty()) ResponseEntity.ok().build()
            else ResponseEntity.status(409).build()
        }
    }
}