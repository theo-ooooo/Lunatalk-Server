package kr.co.lunatalk.domain.auth.service;

import kr.co.lunatalk.domain.auth.config.KakaoOAuthProperties;
import kr.co.lunatalk.domain.auth.dto.response.AuthTokenResponse;
import kr.co.lunatalk.domain.auth.dto.response.KakaoTokenResponse;
import kr.co.lunatalk.domain.auth.dto.response.KakaoUserInfoResponse;
import kr.co.lunatalk.domain.auth.dto.response.TokenResponse;
import kr.co.lunatalk.domain.member.domain.Member;
import kr.co.lunatalk.domain.member.domain.MemberRole;
import kr.co.lunatalk.domain.member.domain.MemberStatus;
import kr.co.lunatalk.domain.member.domain.Profile;
import kr.co.lunatalk.domain.member.repository.MemberRepository;
import kr.co.lunatalk.global.exception.CustomException;
import kr.co.lunatalk.global.exception.ErrorCode;
import kr.co.lunatalk.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class KakaoOAuthService {

	private static final String PROVIDER = "KAKAO";
	private final KakaoOAuthProperties kakaoOAuthProperties;
	private final MemberRepository memberRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final RestTemplate restTemplate;

	public AuthTokenResponse login(String authorizationCode) {
		// 1. 카카오 액세스 토큰 발급
		String accessToken = getAccessToken(authorizationCode);

		// 2. 카카오 사용자 정보 조회
		KakaoUserInfoResponse userInfo = getUserInfo(accessToken);

		// 3. 회원 조회 또는 생성
		Member member = findOrCreateMember(userInfo);

		// 4. JWT 토큰 발급
		TokenResponse tokenResponse = jwtTokenProvider.generateTokenPair(member.getId(), member.getRole());

		return AuthTokenResponse.from(tokenResponse);
	}

	private String getAccessToken(String authorizationCode) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", kakaoOAuthProperties.getClientId());
		params.add("client_secret", kakaoOAuthProperties.getClientSecret());
		params.add("redirect_uri", kakaoOAuthProperties.getRedirectUri());
		params.add("code", authorizationCode);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

		try {
			ResponseEntity<KakaoTokenResponse> response = restTemplate.postForEntity(
				kakaoOAuthProperties.getTokenUri(),
				request,
				KakaoTokenResponse.class
			);

			if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
				return response.getBody().accessToken();
			}

			throw new CustomException(ErrorCode.OAUTH_TOKEN_REQUEST_FAILED);
		} catch (Exception e) {
			log.error("카카오 액세스 토큰 발급 실패: {}", e.getMessage());
			throw new CustomException(ErrorCode.OAUTH_TOKEN_REQUEST_FAILED);
		}
	}

	private KakaoUserInfoResponse getUserInfo(String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> request = new HttpEntity<>(headers);

		try {
			ResponseEntity<KakaoUserInfoResponse> response = restTemplate.exchange(
				kakaoOAuthProperties.getUserInfoUri(),
				HttpMethod.GET,
				request,
				KakaoUserInfoResponse.class
			);

			if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
				return response.getBody();
			}

			throw new CustomException(ErrorCode.OAUTH_USER_INFO_REQUEST_FAILED);
		} catch (Exception e) {
			log.error("카카오 사용자 정보 조회 실패: {}", e.getMessage());
			throw new CustomException(ErrorCode.OAUTH_USER_INFO_REQUEST_FAILED);
		}
	}

	private Member findOrCreateMember(KakaoUserInfoResponse userInfo) {
		String providerId = String.valueOf(userInfo.id());

		// 기존 소셜 계정 조회
		Optional<Member> existingMember = memberRepository.findByProviderAndProviderId(
			PROVIDER,
			providerId
		);

		if (existingMember.isPresent()) {
			Member member = existingMember.get();
			if (member.getStatus() == MemberStatus.DELETE) {
				throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
			}
			member.updateLastLoginAt();
			return member;
		}

		// 신규 회원 생성
		String email = userInfo.kakaoAccount() != null && userInfo.kakaoAccount().email() != null
			? userInfo.kakaoAccount().email()
			: userInfo.id() + "@kakao.com";

		String nickname = userInfo.kakaoAccount() != null
			&& userInfo.kakaoAccount().profile() != null
			&& userInfo.kakaoAccount().profile().nickname() != null
			? userInfo.kakaoAccount().profile().nickname()
			: userInfo.id().toString();

		String profileImageUrl = userInfo.kakaoAccount() != null
			&& userInfo.kakaoAccount().profile() != null
			? userInfo.kakaoAccount().profile().profileImageUrl()
			: "";

		Profile profile = Profile.of(nickname, profileImageUrl);
		String username = PROVIDER.toLowerCase() + "_" + userInfo.id();

		Member newMember = Member.createSocialMember(
			username,
			profile,
			email,
			PROVIDER,
			providerId
		);

		return memberRepository.save(newMember);
	}
}

