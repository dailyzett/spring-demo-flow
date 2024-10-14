package com.example.paymentdemo.domain

open class Event {
}

data class PaymentEvent(
    val userEmail: String,
    val amount: Int,
): Event()