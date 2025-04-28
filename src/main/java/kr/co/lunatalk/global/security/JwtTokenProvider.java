package kr.co.lunatalk.global.security;

import kr.co.lunatalk.domain.auth.domain.RefreshToken;
import kr.co.lunatalk.domain.auth.dto.AccessTokenDto;
import kr.co.lunatalk.domain.auth.dto.RefreshTokenDto;
import kr.co.lunatalk.domain.auth.dto.response.TokenResponse;
import kr.co.lunatalk.domain.auth.repository.RefreshRepository;
import kr.co.lunatalk.domain.member.domain.MemberRole;
import kr.co.lunatalk.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JwtTokenProvider {
	private final JwtUtil jwtUtil;
	private final RefreshRepository refreshRepository;

	public TokenResponse generateTokenPair(Long memberId, MemberRole memberRole) {
		String accessToken = createAccessToken(memberId, memberRole);
		String refreshToken = createRefreshToken(memberId, memberRole);

		return TokenResponse.of(accessToken, refreshToken);
	}

	public String createAccessToken(Long memberId, MemberRole memberRole) {
		return jwtUtil.generateAccessToken(memberId, memberRole);
	}

	public String createRefreshToken(Long memberId, MemberRole memberRole) {
		String refreshToken = jwtUtil.generateRefreshToken(memberId, memberRole);
		saveRefreshTokenToRedis(memberId, refreshToken, jwtUtil.getRefreshTokenExpirationTime());
		return refreshToken;
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

	public RefreshTokenDto retrieveRefreshToken(String tokenValue) {
		RefreshTokenDto refreshTokenDto = parseRefreshToken(tokenValue);

		if (refreshTokenDto == null) {
			return null;
		}

		Optional<RefreshToken> optionalRefreshToken = getRefreshTokenFromRedis(refreshTokenDto.memberId());

		if (optionalRefreshToken.isEmpty()) {
			return null;
		}
		RefreshToken refreshToken = optionalRefreshToken.get();

		if(!(refreshToken.getRefreshToken().equals(tokenValue))) {
			return null;
		}
		return refreshTokenDto;
	}



	private Optional<RefreshToken> getRefreshTokenFromRedis(Long memberId) {
		return refreshRepository.findById(memberId);
	}

	private void saveRefreshTokenToRedis(Long memberId, String refreshTokenValue, Long ttl) {
		RefreshToken refreshToken = RefreshToken.of(memberId, refreshTokenValue, ttl);
		refreshRepository.save(refreshToken);
	}

	public void deleteRefreshTokenFromRedis(Long memberId) {
		refreshRepository.deleteById(memberId);
	}
}
