package kr.co.lunatalk.domain.auth.dto.response

import io.swagger.v3.oas.annotations.media.Schema

data class AuthTokenResponse(
    @Schema(description = "액세스 토큰")
    val accessToken: String,
    @Schema(description = "리프레쉬 토큰")
    val refreshToken: String,
) {
    companion object {
        fun from(tokenResponse: TokenResponse): AuthTokenResponse =
            AuthTokenResponse(
                accessToken = tokenResponse.accessToken,
                refreshToken = tokenResponse.refreshToken,
            )
    }
}
