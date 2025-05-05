package me.keraktelor.repositories.auth

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime

object Users : UUIDTable("users") {
    val username = text("username").uniqueIndex()
    val isActive = bool("is_active").index()
    val password = text("password")
    val lastLogin = datetime("last_login")
    val createdAt = datetime("created_at")
}
