package me.keraktelor.repositories.auth

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import me.keraktelor.utilities.datetime.localDateTimeNow
import java.util.*

data class User(
    val id: UUID,
    val username: String,
    val password: String,
    val lastLogin: LocalDateTime?,
    val createdAt: LocalDateTime,
) {
    constructor(username: String, password: String) : this(
        id = UUID.randomUUID(),
        username = username,
        password = password,
        lastLogin = null,
        createdAt = localDateTimeNow(),
    )

    companion object {
        fun UserEntity.toUser(): User = User(
            id = id.value,
            username = username,
            password = password,
            lastLogin = lastLogin?.toKotlinLocalDateTime(),
            createdAt = createdAt.toKotlinLocalDateTime(),
        )
    }
}
