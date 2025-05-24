package services.auth.impl

import services.auth.AuthServiceImpl
import services.auth.JwtManager
import services.auth.RefreshServiceReq
import services.auth.RefreshServiceRes

internal suspend fun AuthServiceImpl.refreshImpl(
    request: RefreshServiceReq,
): RefreshServiceRes = when (
    val decoded = jwt.decodeRefresh(
        request.refreshToken,
    )
) {
    JwtManager.DecodeResult.Expired -> {
        RefreshServiceRes.Error.RefreshTokenExpired
    }

    JwtManager.DecodeResult.Invalid -> {
        RefreshServiceRes.Error.RefreshTokenInvalid
    }

    is JwtManager.DecodeResult.Ok -> {
        RefreshServiceRes.Ok(
            accessToken = jwt.generateAccess(decoded.subject),
        )
    }
}
