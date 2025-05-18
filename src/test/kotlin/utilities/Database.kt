package utilities

import de.neonew.exposed.migrations.runMigrations
import migrations.migrations
import org.jetbrains.exposed.sql.Database

private const val PGSQL_COMPATIBILITY =
    "MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH"

fun getTestDatabase(): Database = Database
    .connect(
        "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;$PGSQL_COMPATIBILITY",
        driver = "org.postgresql.Driver",
    ).also {
        runMigrations(migrations)
    }
