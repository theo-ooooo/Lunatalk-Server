package kr.co.lunatalk.global.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import kr.co.lunatalk.domain.auth.dto.AccessTokenDto;
import kr.co.lunatalk.domain.auth.dto.RefreshTokenDto;
import kr.co.lunatalk.domain.member.domain.MemberRole;
import kr.co.lunatalk.infra.jwt.JwtProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtil {

	private final JwtProperties jwtProperties;

	public String generateAccessToken(Long memberId, MemberRole memberRole) {
		Date issuedAt = new Date();
		Date expiredAt = new Date(issuedAt.getTime() + jwtProperties.getAccessTokenExpirationTime());

		return generateToken(memberId, memberRole, issuedAt, expiredAt, getAccessTokenKey());
	}

	public String generateRefreshToken(Long memberId, MemberRole memberRole) {
		Date issuedAt = new Date();
		Date expiredAt = new Date(issuedAt.getTime() + jwtProperties.getRefreshTokenExpirationTime());

		return generateToken(memberId, memberRole, issuedAt, expiredAt, getRefreshTokenKey());
	}

	public AccessTokenDto parseAccessToken(String token) throws ExpiredJwtException {
		try {
			Jws<Claims> claims = getClaims(token, getAccessTokenKey());

			return new AccessTokenDto(
				Long.parseLong(claims.getBody().getSubject()),
				MemberRole.valueOf(claims.getBody().get("role", String.class))
			);
		} catch (ExpiredJwtException e) {
			throw e;
		} catch (Exception e) {
			return null;
		}
	}

	public RefreshTokenDto parseRefreshToken(String token) throws ExpiredJwtException {
		try {
			Jws<Claims> claims = getClaims(token, getRefreshTokenKey());

			return new RefreshTokenDto(
					Long.parseLong(claims.getBody().getSubject()),
					MemberRole.valueOf(claims.getBody().get("role", String.class)),
					jwtProperties.refreshTokenExpirationTime()
			);
		} catch (ExpiredJwtException e) {
			throw e;
		} catch (Exception e) {
			return null;
		}
	}



	private Key getAccessTokenKey() {
		return Keys.hmacShaKeyFor(jwtProperties.accessTokenSecret().getBytes());
	}

	private Key getRefreshTokenKey() {
		return Keys.hmacShaKeyFor(jwtProperties.refreshTokenSecret().getBytes());
	}

	private Jws<Claims> getClaims(String token, Key key) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
	}

	public String generateToken(Long memberId, MemberRole memberRole, Date issuedAt, Date expiredAt, Key key) {
		return Jwts.builder()
			.setSubject(memberId.toString())
			.claim("role", memberRole.name())
			.setIssuedAt(issuedAt)
			.setExpiration(expiredAt)
			.signWith(key)
			.compact();
	}


	public Long getRefreshTokenExpirationTime() {
		return jwtProperties.refreshTokenExpirationTime();
	}

}
