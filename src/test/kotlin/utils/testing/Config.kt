package me.keraktelor.utils.testing

import me.keraktelor.setup.Config

fun getTestConfig(): Config = Config(
    postgres = Config.Postgres(
        auth = Config.Postgres.Auth(
            username = "test",
            password = "test",
        ),
        connection = Config.Postgres.Connection(
            host = "test",
            port = 0,
        ),
        database = "test",
    ),
    crypto = Config.Crypto(
        bcrypt = Config.Crypto.BCrypt(
            rounds = 10,
        ),
    ),
)
