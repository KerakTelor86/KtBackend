package me.keraktelor.repositories.auth

import kotlinx.datetime.LocalDateTime
import java.util.*

data class User(
    val id: UUID,
    val username: String,
    val password: String,
    val lastLogin: LocalDateTime?,
    val createdAt: LocalDateTime,
)
