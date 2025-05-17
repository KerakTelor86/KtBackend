package utilities

import org.jetbrains.exposed.sql.Database

fun getTestDatabase(): Database = Database.connect(
    "jdbc:h2:mem:test",
    driver = "org.h2.Driver",
)
