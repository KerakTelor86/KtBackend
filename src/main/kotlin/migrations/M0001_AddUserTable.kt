package me.keraktelor.migrations

import de.neonew.exposed.migrations.helpers.AddTableMigration
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

@Suppress("ClassName", "unused")
class M0001_AddUserTable : AddTableMigration() {
    private object Users : UUIDTable("users") {
        init {
            text("username").uniqueIndex()
            bool("is_active").index()
            text("password")
            datetime("last_login")
            datetime("created_at")
        }
    }

    override val tables: Array<Table>
        get() = arrayOf(
            Users,
        )
}
