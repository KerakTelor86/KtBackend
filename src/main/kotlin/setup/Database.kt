package me.keraktelor.setup

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

fun Application.setupDatabase() {
    fun getPgConfig(path: String): String =
        environment.config.property("database.postgres.$path").getString()

    val dbName = getPgConfig("database")
    val dbAddress =
        "${getPgConfig("connection.host")}:${getPgConfig("connection.port")}"

    val hikariConfig = HikariConfig().apply {
        dataSourceClassName = "org.postgresql.ds.PGSimpleDataSource"
        dataSource.apply {
            jdbcUrl = "jdbc:postgresql://$dbAddress/$dbName"
            username = getPgConfig("auth.username")
            password = getPgConfig("auth.password")
        }
    }

    val hikariDataSource = HikariDataSource(hikariConfig)

    Database.connect(hikariDataSource)
}
