package kr.co.lunatalk.domain.auth.dto.response;

public record AuthTokenResponse(String accessToken, String refreshToken) {
	public static AuthTokenResponse from(TokenResponse tokenResponse) {
		return new AuthTokenResponse(tokenResponse.accessToken(), tokenResponse.refreshToken());
	}
}
