package kr.co.lunatalk.infra.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
	String accessTokenSecret,
	String refreshTokenSecret,
	Long accessTokenExpirationTime,
	Long refreshTokenExpirationTime,
	String issuer
) {
	public Long getAccessTokenExpirationTime() {
		return accessTokenExpirationTime * 1000;
	}
	public Long getRefreshTokenExpirationTime() {
		return refreshTokenExpirationTime * 1000;
	}
}
