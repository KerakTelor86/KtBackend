package me.keraktelor.migrations

import de.neonew.exposed.migrations.helpers.AddTableMigration
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

@Suppress("ClassName", "unused")
class M0001_AddUserTable : AddTableMigration() {
    private object UsersTable : UUIDTable("users") {
        init {
            text("username").uniqueIndex()
            text("password")
            bool("is_active").index().default(true)
            datetime("last_login").nullable().default(null)
            datetime("created_at")
        }
    }

    override val tables: Array<Table>
        get() = arrayOf(
            UsersTable,
        )
}
