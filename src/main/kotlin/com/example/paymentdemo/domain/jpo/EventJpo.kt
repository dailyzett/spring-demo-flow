package com.example.paymentdemo.domain.jpo

import com.example.paymentdemo.domain.enum.EnumEventType
import jakarta.persistence.*
import java.io.Serializable
import java.time.LocalDateTime

@Entity
@Table(name = "events")
class EventJpo (
    @EmbeddedId
    val id: EventKey,

    val amount: Int,
)

@Embeddable
data class EventKey(
    @Column(name = "created_dt", nullable = false)
    val createdDt: LocalDateTime,

    @Column(name = "event_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    val eventType: EnumEventType,

    @Column(name = "email", nullable = false, length = 100)
    val email: String,
) : Serializable