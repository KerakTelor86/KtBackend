package services.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import setup.Config
import utilities.datetime.instantNow
import kotlin.time.Duration.Companion.seconds

internal class JwtManager(
    config: Config,
) {
    fun generateAccess(subject: String): String = TokenClass
        .Access
        .create(
            subject,
            validUntil = instantNow() + accessValidity,
        )

    fun generateRefresh(subject: String): String = TokenClass
        .Refresh
        .create(
            subject,
            validUntil = instantNow() + refreshValidity,
        )

    fun decodeAccess(token: String): DecodeResult = TokenClass
        .Access
        .decode(token)

    fun decodeRefresh(token: String): DecodeResult = TokenClass
        .Refresh
        .decode(token)

    private val issuer =
        config.crypto.jwt.issuer
    private val accessValidity =
        config.crypto.jwt.expirySeconds.access.seconds
    private val refreshValidity =
        config.crypto.jwt.expirySeconds.refresh.seconds

    private val algo = Algorithm.HMAC256(config.crypto.jwt.secret)

    private fun TokenClass.create(
        subject: String,
        validUntil: Instant,
    ): String = JWT
        .create()
        .withIssuer(issuer)
        .withSubject(subject)
        .withClaim(TOKEN_CLASS_KEY, this.name)
        .withExpiresAt(validUntil.toJavaInstant())
        .withIssuedAt(instantNow().toJavaInstant())
        .sign(algo)

    private val accessVerifier = JWT
        .require(algo)
        .withIssuer(issuer)
        .withClaim(TOKEN_CLASS_KEY, TokenClass.Access.name)
        .build()
    private val refreshVerifier = JWT
        .require(algo)
        .withIssuer(issuer)
        .withClaim(TOKEN_CLASS_KEY, TokenClass.Refresh.name)
        .build()

    private fun TokenClass.decode(token: String): DecodeResult {
        val verifier = when (this) {
            TokenClass.Access -> accessVerifier
            TokenClass.Refresh -> refreshVerifier
        }

        val decoded = try {
            verifier.verify(token)
        } catch (_: JWTVerificationException) {
            return DecodeResult.Invalid
        }

        val expiresAt = decoded.expiresAtAsInstant?.toKotlinInstant()
            ?: Instant.DISTANT_PAST
        if (instantNow() > expiresAt) {
            return DecodeResult.Expired
        }

        return DecodeResult.Ok(
            subject = decoded.subject.orEmpty(),
        )
    }

    sealed class DecodeResult {
        data object Invalid : DecodeResult()

        data object Expired : DecodeResult()

        data class Ok(
            val subject: String,
        ) : DecodeResult()
    }

    private companion object {
        const val TOKEN_CLASS_KEY = "cls"

        private enum class TokenClass {
            Access,
            Refresh,
        }
    }
}
