package kr.co.lunatalk.domain.auth.service;

import kr.co.lunatalk.TestRedisConfig;
import kr.co.lunatalk.domain.auth.dto.request.LoginRequest;
import kr.co.lunatalk.domain.auth.dto.response.AuthTokenResponse;
import kr.co.lunatalk.domain.auth.dto.response.TokenResponse;
import kr.co.lunatalk.domain.member.domain.Member;
import kr.co.lunatalk.domain.member.domain.MemberRole;
import kr.co.lunatalk.domain.member.domain.Profile;
import kr.co.lunatalk.domain.member.dto.request.CreateMemberRequest;
import kr.co.lunatalk.domain.member.repository.MemberRepository;
import kr.co.lunatalk.global.exception.CustomException;
import kr.co.lunatalk.global.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("인증 서비스 단위테스트")
@Import({TestRedisConfig.class})
class AuthServiceTest {

	@Mock
	MemberRepository memberRepository;

	@Mock
	PasswordEncoder passwordEncoder;

	@Mock
	JwtTokenProvider jwtTokenProvider;

	@InjectMocks
	AuthService authService;

	@Test
	void 회원가입() {
		TokenResponse tempTokenPair = new TokenResponse("accessToken", "refreshToken");

		Member newMember = Member.of("username", "password", Profile.of("", ""));


		when(memberRepository.findByUsername("username")).thenReturn(Optional.empty());
		when(memberRepository.save(any(Member.class))).thenReturn(newMember);
		when(jwtTokenProvider.generateTokenPair(newMember.getId(), newMember.getRole())).thenReturn(tempTokenPair);

		AuthTokenResponse response = authService.registerMember(new CreateMemberRequest("username", "password"));

		assertNotNull(response);
		assertEquals("accessToken", response.accessToken());
		assertEquals("refreshToken", response.refreshToken());
	}

	@Test
	void 로그인() {
		Member member = Member.of("login", "password", Profile.of("", ""));
		when(memberRepository.findByUsername("login")).thenReturn(Optional.of(member));

		when(jwtTokenProvider.generateTokenPair(member.getId(), member.getRole())).thenReturn(new TokenResponse("accessToken", "refreshToken"));
		AuthTokenResponse response = authService.loginMember(new LoginRequest("login", "password"));

		assertNotNull(response);
		assertEquals("accessToken", response.accessToken());
		assertEquals("refreshToken", response.refreshToken());
	}
}
