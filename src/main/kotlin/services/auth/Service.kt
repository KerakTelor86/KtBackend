package services.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import org.mindrot.jbcrypt.BCrypt
import repositories.auth.AuthRepository
import repositories.auth.User
import setup.Config
import utilities.datetime.instantNow
import kotlin.time.Duration.Companion.seconds

internal class AuthServiceImpl(
    config: Config,
    private val authRepository: AuthRepository,
) : AuthService {
    private val bcryptRounds = config.crypto.bcrypt.rounds

    private val jwtIssuer =
        config.crypto.jwt.issuer
    private val jwtAccessValidity =
        config.crypto.jwt.expirySeconds.access.seconds
    private val jwtRefreshValidity =
        config.crypto.jwt.expirySeconds.refresh.seconds

    override suspend fun register(
        request: RegisterServiceReq,
    ): RegisterServiceRes {
        val user = authRepository.create(
            username = request.username,
            password = BCrypt.hashpw(
                request.password,
                BCrypt.gensalt(bcryptRounds),
            ),
        ) ?: return RegisterServiceRes.Error.DuplicateUser

        return RegisterServiceRes.Ok(
            userId = user.id.toString(),
            tokens = generateTokens(user),
        )
    }

    override suspend fun login(request: LoginServiceReq): LoginServiceRes {
        val user = authRepository.findByUsername(request.username)
            ?: return LoginServiceRes.Error.InvalidCredentials

        return when (BCrypt.checkpw(request.password, user.password)) {
            true -> LoginServiceRes.Ok(user.id.toString(), generateTokens(user))
            false -> LoginServiceRes.Error.InvalidCredentials
        }
    }

    override suspend fun refresh(
        request: RefreshServiceReq,
    ): RefreshServiceRes {
        val decoded = try {
            jwtRefreshDecoder.verify(request.refreshToken)
        } catch (_: JWTVerificationException) {
            return RefreshServiceRes.Error.RefreshTokenInvalid
        }

        val currentTime = instantNow()
        if (currentTime > decoded.expiresAtAsInstant.toKotlinInstant()) {
            return RefreshServiceRes.Error.RefreshTokenExpired
        }

        return RefreshServiceRes.Ok(
            accessToken = TokenClass.Access.createFor(
                decoded.subject,
                validUntil = currentTime + jwtAccessValidity,
            ),
        )
    }

    override suspend fun decode(
        request: DecodeTokenServiceReq,
    ): DecodeTokenServiceRes {
        val decoded = try {
            jwtAccessDecoder.verify(request.accessToken)
        } catch (_: JWTVerificationException) {
            return DecodeTokenServiceRes.Error.AccessTokenInvalid
        }

        val currentTime = instantNow()
        if (currentTime > decoded.expiresAtAsInstant.toKotlinInstant()) {
            return DecodeTokenServiceRes.Error.AccessTokenExpired
        }

        return DecodeTokenServiceRes.Ok(
            userId = decoded.subject,
        )
    }

    private fun generateTokens(user: User): AuthToken {
        val currentTime = instantNow()
        val subject = user.id.toString()
        return AuthToken(
            access = TokenClass.Access.createFor(
                subject,
                validUntil = currentTime + jwtAccessValidity,
            ),
            refresh = TokenClass.Refresh.createFor(
                subject,
                validUntil = currentTime + jwtRefreshValidity,
            ),
        )
    }

    private fun TokenClass.createFor(
        subject: String,
        validUntil: Instant,
    ): String = JWT
        .create()
        .withIssuer(jwtIssuer)
        .withSubject(subject)
        .withClaim(TOKEN_CLASS_KEY, this.ordinal)
        .withExpiresAt(validUntil.toJavaInstant())
        .withIssuedAt(instantNow().toJavaInstant())
        .sign(jwtAlgo)

    private val jwtAlgo = Algorithm.HMAC256(config.crypto.jwt.secret)
    private val jwtAccessDecoder = JWT
        .require(jwtAlgo)
        .withIssuer(jwtIssuer)
        .withClaim(TOKEN_CLASS_KEY, TokenClass.Access.ordinal)
        .build()
    private val jwtRefreshDecoder = JWT
        .require(jwtAlgo)
        .withIssuer(jwtIssuer)
        .withClaim(TOKEN_CLASS_KEY, TokenClass.Access.ordinal)
        .build()

    private companion object {
        enum class TokenClass {
            Access,
            Refresh,
        }

        const val TOKEN_CLASS_KEY = "cls"
    }
}
