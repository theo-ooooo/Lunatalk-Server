package kr.co.lunatalk.global.security;

import kr.co.lunatalk.domain.auth.dto.AccessTokenDto;
import kr.co.lunatalk.domain.auth.dto.RefreshTokenDto;
import kr.co.lunatalk.domain.auth.dto.response.TokenResponse;
import kr.co.lunatalk.domain.member.domain.MemberRole;
import kr.co.lunatalk.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtTokenProvider {
	private final JwtUtil jwtUtil;

	public TokenResponse generateTokenPair(Long memberId, MemberRole memberRole) {
		String accessToken = createAccessToken(memberId, memberRole);
		String refreshToken = createRefreshToken(memberId, memberRole);

		return TokenResponse.of(accessToken, refreshToken);
	}

	public String createAccessToken(Long memberId, MemberRole memberRole) {
		return jwtUtil.generateAccessToken(memberId, memberRole);
	}

	public String createRefreshToken(Long memberId, MemberRole memberRole) {
		return jwtUtil.generateRefreshToken(memberId, memberRole);
	}

	public AccessTokenDto parseAccessToken(String token) {
		try {
			return jwtUtil.parseAccessToken(token);
		} catch (Exception e) {
			return null;
		}
	}

	public RefreshTokenDto parseRefreshToken(String token) {
		try {
			return jwtUtil.parseRefreshToken(token);
		} catch (Exception e) {
			return null;
		}
	}
}
