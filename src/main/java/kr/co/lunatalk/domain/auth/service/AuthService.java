package kr.co.lunatalk.domain.auth.service;

import jakarta.transaction.Transactional;
import kr.co.lunatalk.domain.auth.dto.request.LoginRequest;
import kr.co.lunatalk.domain.auth.dto.response.AuthTokenResponse;
import kr.co.lunatalk.domain.auth.dto.response.TokenResponse;
import kr.co.lunatalk.domain.member.domain.Member;
import kr.co.lunatalk.domain.member.domain.MemberRole;
import kr.co.lunatalk.domain.member.domain.Profile;
import kr.co.lunatalk.domain.member.dto.request.CreateMemberRequest;
import kr.co.lunatalk.domain.member.repository.MemberRepository;
import kr.co.lunatalk.global.exception.CustomException;
import kr.co.lunatalk.global.exception.ErrorCode;
import kr.co.lunatalk.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;


	public AuthTokenResponse registerMember(CreateMemberRequest request) {

		Optional<Member> existsMember = memberRepository.findByUsername(request.username());
		if (existsMember.isPresent()) {
			throw new CustomException(ErrorCode.MEMBER_EXISTS);
		}

		Member member = Member.of(request.username(), encodePassword(request.password()), Profile.of("", ""));
		memberRepository.save(member);

		TokenResponse token = generateToken(member.getId(), member.getRole());

		return AuthTokenResponse.from(token);
	}

	public AuthTokenResponse loginMember(LoginRequest request) {
		Optional<Member> findMember = memberRepository.findByUsername(request.username());
		if(findMember.isPresent()) {
			final Member member = findMember.get();
			Boolean isMatching = matchingPassword(request.password(), member.getPassword());
			if(isMatching) {
				TokenResponse token = generateToken(member.getId(), member.getRole());
				return AuthTokenResponse.from(token);
			}
		}
		throw new CustomException(ErrorCode.AUTH_UNAUTHORIZED);
	}

	private String encodePassword(String password) {
		return passwordEncoder.encode(password);
	}

	private Boolean matchingPassword(String password, String encodedPassword) {
		return passwordEncoder.matches(password, encodedPassword);
	}

	private TokenResponse generateToken(Long memberId, MemberRole memberRole) {
		String accessToken = jwtUtil.generateAccessToken(memberId, memberRole);
		String refreshToken = jwtUtil.generateRefreshToken(memberId, memberRole);

		return TokenResponse.of(accessToken, refreshToken);
	}
}
