package com.example.paymentdemo.domain.jpo

import org.springframework.data.jpa.repository.JpaRepository

interface UserJpoRepository: JpaRepository<UserJpo, String> {
}