package services.auth.impl

import services.auth.AuthServiceImpl
import services.auth.DecodeTokenServiceReq
import services.auth.DecodeTokenServiceRes
import services.auth.JwtManager

internal fun AuthServiceImpl.decodeImpl(
    request: DecodeTokenServiceReq,
): DecodeTokenServiceRes = when (
    val decoded = jwt.decodeAccess(
        request.accessToken,
    )
) {
    JwtManager.DecodeResult.Expired -> {
        DecodeTokenServiceRes.Error.AccessTokenExpired
    }

    JwtManager.DecodeResult.Invalid -> {
        DecodeTokenServiceRes.Error.AccessTokenInvalid
    }

    is JwtManager.DecodeResult.Ok -> {
        DecodeTokenServiceRes.Ok(
            userId = decoded.subject,
        )
    }
}
