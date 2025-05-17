package utilities

import setup.Config

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
        jwt = Config.Crypto.Jwt(
            secret = "testSecret123!",
            issuer = "test_iss",
            expirySeconds = Config.Crypto.Jwt.Expiry(
                access = 30,
                refresh = 150,
            ),
        ),
    ),
)
