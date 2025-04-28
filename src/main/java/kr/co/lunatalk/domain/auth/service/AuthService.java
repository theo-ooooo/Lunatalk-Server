package kr.co.lunatalk.domain.auth.service;

import kr.co.lunatalk.domain.auth.dto.RefreshTokenDto;
import kr.co.lunatalk.domain.auth.dto.request.LoginRequest;
import kr.co.lunatalk.domain.auth.dto.request.RefreshTokenRequest;
import kr.co.lunatalk.domain.auth.dto.response.AuthTokenResponse;
import kr.co.lunatalk.domain.auth.dto.response.TokenResponse;
import kr.co.lunatalk.domain.member.domain.Member;
import kr.co.lunatalk.domain.member.domain.MemberStatus;
import kr.co.lunatalk.domain.member.domain.Profile;
import kr.co.lunatalk.domain.member.dto.request.CreateMemberRequest;
import kr.co.lunatalk.domain.member.repository.MemberRepository;
import kr.co.lunatalk.global.exception.CustomException;
import kr.co.lunatalk.global.exception.ErrorCode;
import kr.co.lunatalk.global.security.JwtTokenProvider;
import kr.co.lunatalk.global.util.MemberUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final MemberUtil memberUtil;


	public AuthTokenResponse registerMember(CreateMemberRequest request) {

		Optional<Member> existsMember = memberRepository.findByUsername(request.username());
		if (existsMember.isPresent()) {
			throw new CustomException(ErrorCode.MEMBER_EXISTS);
		}

		Member member = Member.of(request.username(), encodePassword(request.password()), Profile.of("", ""));
		memberRepository.save(member);

		TokenResponse token = getTokenResponse(member);

		return AuthTokenResponse.from(token);
	}

	public AuthTokenResponse loginMember(LoginRequest request) {
		Optional<Member> findMember = memberRepository.findByUsername(request.username());

		if (findMember.isEmpty()) {
			throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
		}

		Member member = findMember.get();

		if (member.getStatus() == MemberStatus.DELETE) {
			throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
		}

		Boolean isMatching = matchingPassword(request.password(), member.getPassword());

		if(!isMatching) {
			throw new CustomException(ErrorCode.AUTH_UNAUTHORIZED);
		}

		return AuthTokenResponse.from(getTokenResponse(member));
	}

	public void withdraw() {
		Member currentMember = memberUtil.getCurrentMember();

		if(currentMember.getStatus() == MemberStatus.DELETE) {
			throw new CustomException(ErrorCode.MEMBER_ALREADY_DELETED);
		}
		currentMember.withdrawal();
		memberRepository.deleteById(currentMember.getId());
		jwtTokenProvider.deleteRefreshTokenFromRedis(currentMember.getId());
	}

	private TokenResponse getTokenResponse(Member member) {
		return jwtTokenProvider.generateTokenPair(member.getId(), member.getRole());
	}

	@Transactional(readOnly = true)
	public AuthTokenResponse reissueTokenPair(RefreshTokenRequest request) {
		RefreshTokenDto refreshTokenDto = jwtTokenProvider.retrieveRefreshToken(request.refreshToken());

		if(refreshTokenDto == null) {
			throw new CustomException(ErrorCode.AUTH_TOKEN_EXPIRED);
		}

		Optional<Member> findMember = memberRepository.findById(refreshTokenDto.memberId());

		if (findMember.isEmpty()) {
			throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
		}

		Member member = findMember.get();

		return AuthTokenResponse.from(getTokenResponse(member));
	}

	private String encodePassword(String password) {
		return passwordEncoder.encode(password);
	}

	private Boolean matchingPassword(String password, String encodedPassword) {
		return passwordEncoder.matches(password, encodedPassword);
	}
}
