package me.keraktelor.setup

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import de.neonew.exposed.migrations.runMigrations
import me.keraktelor.migrations.migrations
import org.jetbrains.exposed.sql.Database
import org.koin.core.scope.Scope
import org.koin.dsl.module

val databaseModule = module {
    single(createdAtStart = true) {
        setupDatabase()
    }
}

fun Scope.setupDatabase(): Database {
    val hikariConfig by inject<HikariConfig>()
    val hikariDataSource = HikariDataSource(hikariConfig)

    return Database.connect(hikariDataSource).also {
        runMigrations(migrations)
    }
}
