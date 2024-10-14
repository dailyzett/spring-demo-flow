package com.example.paymentdemo.domain.jpo

import org.springframework.data.jpa.repository.JpaRepository

interface EventsJpoRepository : JpaRepository<EventJpo, EventKey> {
    fun countById(key: EventKey): Int
}