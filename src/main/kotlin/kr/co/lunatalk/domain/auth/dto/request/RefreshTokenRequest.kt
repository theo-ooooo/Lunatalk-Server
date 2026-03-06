package kr.co.lunatalk.domain.auth.dto.request

import io.swagger.v3.oas.annotations.media.Schema

data class RefreshTokenRequest(
    @Schema(description = "리프레쉬 토큰")
    val refreshToken: String,
)
