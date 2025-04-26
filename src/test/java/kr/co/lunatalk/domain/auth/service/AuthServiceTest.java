package kr.co.lunatalk.domain.auth.service;

import kr.co.lunatalk.FixtureMonkeySetUp;
import kr.co.lunatalk.TestRedisConfig;
import kr.co.lunatalk.domain.auth.dto.request.LoginRequest;
import kr.co.lunatalk.domain.auth.dto.response.AuthTokenResponse;
import kr.co.lunatalk.domain.member.domain.Member;
import kr.co.lunatalk.domain.member.domain.Profile;
import kr.co.lunatalk.domain.member.repository.MemberRepository;
import kr.co.lunatalk.infra.config.redis.RedisProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;


@ActiveProfiles("test")
@Import({TestRedisConfig.class})
@DataRedisTest
class AuthServiceTest extends FixtureMonkeySetUp {

	@Autowired
	private AuthService authService;
	@Autowired private MemberRepository memberRepository;

	String username;
	String password;

	@BeforeEach
	void setUp() {
		username = fixtureMonkey.giveMeOne(String.class);
		password = fixtureMonkey.giveMeOne(String.class);
	}

	@Test
	void 일반_로그인() {
		//given

		Member member = Member.of(username, password, Profile.of("test", "test"));
		memberRepository.save(member);
		//when
		AuthTokenResponse authTokenResponse = authService.loginMember(new LoginRequest(username, password));
		//then
		assertNotNull(authTokenResponse.accessToken());
		assertNotNull(authTokenResponse.refreshToken());
	}

	@Test
	void 회원가입() {

	}

	@Test
	void 틸퇴() {

	}

	@Test
	void 토큰_재발급() {

	}
}
