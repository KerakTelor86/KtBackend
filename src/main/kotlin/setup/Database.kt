package setup

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import de.neonew.exposed.migrations.runMigrations
import migrations.migrations
import org.jetbrains.exposed.sql.Database
import org.koin.core.scope.Scope
import org.koin.dsl.module

val databaseModule = module {
    single {
        setupDatabase()
    }
}

fun Scope.setupDatabase(): Database {
    val dbConfig = run {
        val config by inject<Config>()
        config.postgres
    }

    val hikariConfig = HikariConfig().apply {
        dataSourceClassName = "org.postgresql.ds.PGSimpleDataSource"
        dataSourceProperties.apply {
            set("user", dbConfig.auth.username)
            set("password", dbConfig.auth.password)
            set("serverName", dbConfig.connection.host)
            set("portNumber", dbConfig.connection.port)
            set("databaseName", dbConfig.database)
        }
    }

    val hikariDataSource = HikariDataSource(hikariConfig)

    return Database.connect(hikariDataSource).also {
        runMigrations(migrations)
    }
}
