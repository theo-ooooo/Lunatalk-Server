package kr.co.lunatalk.domain.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthTokenResponse(
	@Schema(description = "액세스 토큰")
	String accessToken,
	@Schema(description = "리프레쉬 토큰")
	String refreshToken) {
	public static AuthTokenResponse from(TokenResponse tokenResponse) {
		return new AuthTokenResponse(tokenResponse.accessToken(), tokenResponse.refreshToken());
	}
}
