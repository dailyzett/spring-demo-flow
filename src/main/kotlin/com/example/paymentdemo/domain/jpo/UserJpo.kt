package com.example.paymentdemo.domain.jpo

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class UserJpo (
    @Id
    val email: String,

    var currentBalance: Int
) {
    override fun toString(): String {
        return "UserJpo(email='$email', currentBalance=$currentBalance)"
    }
}