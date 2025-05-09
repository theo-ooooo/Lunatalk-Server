package kr.co.lunatalk.domain.auth.service;

import kr.co.lunatalk.TestRedisConfig;
import kr.co.lunatalk.domain.auth.dto.request.LoginRequest;
import kr.co.lunatalk.domain.auth.dto.response.AuthTokenResponse;
import kr.co.lunatalk.domain.auth.dto.response.TokenResponse;
import kr.co.lunatalk.domain.member.domain.Member;
import kr.co.lunatalk.domain.member.domain.MemberRole;
import kr.co.lunatalk.domain.member.domain.MemberStatus;
import kr.co.lunatalk.domain.member.domain.Profile;
import kr.co.lunatalk.domain.member.dto.request.CreateMemberRequest;
import kr.co.lunatalk.domain.member.repository.MemberRepository;
import kr.co.lunatalk.global.exception.CustomException;
import kr.co.lunatalk.global.exception.ErrorCode;
import kr.co.lunatalk.global.security.JwtTokenProvider;
import kr.co.lunatalk.global.util.MemberUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

	@Spy
	PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Mock
	JwtTokenProvider jwtTokenProvider;

	@Mock
	MemberUtil memberUtil;

	@InjectMocks
	AuthService authService;

	@Test
	void 회원가입() {
		TokenResponse tempTokenPair = new TokenResponse("accessToken", "refreshToken");

		Member newMember = Member.createMember(
			"username",
			"password",
			Profile.of("", ""),
			"01012341234",
			"kkwondev@gmail.com"
		);


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
		Member member = Member.createMember("login", "password", Profile.of("", ""),	"01012341234",
			"kkwondev@gmail.com");
		when(memberRepository.findByUsername("login")).thenReturn(Optional.of(member));

//		when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
		doReturn(true).when(passwordEncoder).matches(anyString(), anyString());

		when(jwtTokenProvider.generateTokenPair(member.getId(), member.getRole())).thenReturn(new TokenResponse("accessToken", "refreshToken"));
		AuthTokenResponse response = authService.loginMember(new LoginRequest("login", "password"));

		assertNotNull(response);
		assertEquals("accessToken", response.accessToken());
		assertEquals("refreshToken", response.refreshToken());
	}

	@Test
	void 탈퇴_기능() {
		Member member = Member.createMember("withdraw", "password", Profile.of("", ""),	"01012341234",
			"kkwondev@gmail.com");

		when(memberUtil.getCurrentMember()).thenReturn(member);

		doAnswer(
			invocationOnMock -> {
				member.withdrawal();
				return null;
			}
		).when(memberRepository).deleteById(member.getId());

		authService.withdraw();

		assertEquals(MemberStatus.DELETE, member.getStatus());

	}

	@Test
	@DisplayName("로그인 실패 - 비밀번호 불일치")
	void 로그인_비밀번호불일치() {
		// given
		String rawPassword = "wrong-password"; // 로그인 요청 비번
		String encodedPassword = passwordEncoder.encode("real-password"); // 저장된 비번 (암호화)

		Member member = Member.createMember("loginUser", encodedPassword, Profile.of("", ""),	"01012341234",
			"kkwondev@gmail.com");

		when(memberRepository.findByUsername("loginUser")).thenReturn(Optional.of(member));
		// when & then
		CustomException exception = assertThrows(CustomException.class, () -> {
			authService.loginMember(new LoginRequest("loginUser", rawPassword));
		});

		assertEquals(ErrorCode.AUTH_UNAUTHORIZED, exception.getErrorCode());
	}
}
