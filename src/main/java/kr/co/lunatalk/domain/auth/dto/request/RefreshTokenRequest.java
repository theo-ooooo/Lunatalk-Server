package kr.co.lunatalk.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record RefreshTokenRequest(
	@Schema(description = "리프레쉬 토큰")
	String refreshToken) {
}
