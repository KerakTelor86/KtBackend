package me.keraktelor.repositories.auth

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.util.*

object UsersTable : UUIDTable("users") {
    val username = text("username").uniqueIndex()
    val password = text("password")
    val isActive = bool("is_active").index().default(true)
    val lastLogin = datetime("last_login").nullable().default(null)
    val createdAt = datetime("created_at")
}

class UserEntity(
    id: EntityID<UUID>,
) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UserEntity>(UsersTable)

    var username by UsersTable.username
    var password by UsersTable.password
    var isActive by UsersTable.isActive
    var lastLogin by UsersTable.lastLogin
    var createdAt by UsersTable.createdAt
}
