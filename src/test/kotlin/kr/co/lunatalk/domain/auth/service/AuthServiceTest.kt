package kr.co.lunatalk.domain.auth.service

import kr.co.lunatalk.TestRedisConfig
import kr.co.lunatalk.domain.auth.dto.request.LoginRequest
import kr.co.lunatalk.domain.auth.dto.response.TokenResponse
import kr.co.lunatalk.domain.member.domain.Member
import kr.co.lunatalk.domain.member.domain.MemberStatus
import kr.co.lunatalk.domain.member.domain.Profile
import kr.co.lunatalk.domain.member.dto.request.CreateMemberRequest
import kr.co.lunatalk.domain.member.repository.MemberRepository
import kr.co.lunatalk.global.exception.CustomException
import kr.co.lunatalk.global.exception.ErrorCode
import kr.co.lunatalk.global.security.JwtTokenProvider
import kr.co.lunatalk.global.util.MemberUtil
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Spy
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.context.annotation.Import
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.util.ReflectionTestUtils
import java.util.*

@ExtendWith(MockitoExtension::class)
@DisplayName("인증 서비스 단위테스트")
@Import(TestRedisConfig::class)
class AuthServiceTest {

    @Mock
    lateinit var memberRepository: MemberRepository

    @Spy
    var passwordEncoder: PasswordEncoder = BCryptPasswordEncoder()

    @Mock
    lateinit var jwtTokenProvider: JwtTokenProvider

    @Mock
    lateinit var memberUtil: MemberUtil

    lateinit var authService: AuthService

    @BeforeEach
    fun setUp() {
        authService = AuthService(memberRepository, passwordEncoder, jwtTokenProvider, memberUtil)
    }

    @Test
    fun `회원가입`() {
        val tempTokenPair = TokenResponse("accessToken", "refreshToken")

        whenever(memberRepository.findByUsername("username")).thenReturn(Optional.empty())
        whenever(memberRepository.save(any<Member>())).thenAnswer { invocation ->
            val saved = invocation.getArgument<Member>(0)
            ReflectionTestUtils.setField(saved, "id", 1L)
            saved
        }
        whenever(jwtTokenProvider.generateTokenPair(eq(1L), any())).thenReturn(tempTokenPair)

        val response = authService.registerMember(
            CreateMemberRequest("username", "password", "01012341234", "kkwondev@gmail.com")
        )

        assertNotNull(response)
        assertEquals("accessToken", response.accessToken)
        assertEquals("refreshToken", response.refreshToken)
    }

    @Test
    fun `로그인`() {
        val member = Member.createMember(
            "login", "password", Profile.of("", ""),
            "01012341234", "kkwondev@gmail.com"
        )
        ReflectionTestUtils.setField(member, "id", 1L)

        whenever(memberRepository.findByUsername("login")).thenReturn(Optional.of(member))
        doReturn(true).whenever(passwordEncoder).matches(any<String>(), any<String>())
        whenever(jwtTokenProvider.generateTokenPair(member.id!!, member.role))
            .thenReturn(TokenResponse("accessToken", "refreshToken"))

        val response = authService.loginMember(LoginRequest("login", "password"))

        assertNotNull(response)
        assertEquals("accessToken", response.accessToken)
        assertEquals("refreshToken", response.refreshToken)
    }

    @Test
    fun `탈퇴 기능`() {
        val member = Member.createMember(
            "withdraw", "password", Profile.of("", ""),
            "01012341234", "kkwondev@gmail.com"
        )
        ReflectionTestUtils.setField(member, "id", 1L)

        whenever(memberUtil.currentMember).thenReturn(member)

        authService.withdraw()

        assertEquals(MemberStatus.DELETE, member.status)
        verify(memberRepository).deleteById(member.id!!)
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    fun `로그인 비밀번호불일치`() {
        // given
        val rawPassword = "wrong-password"
        val encodedPassword = passwordEncoder.encode("real-password")!!

        val member = Member.createMember(
            "loginUser", encodedPassword, Profile.of("", ""),
            "01012341234", "kkwondev@gmail.com"
        )
        ReflectionTestUtils.setField(member, "id", 1L)

        whenever(memberRepository.findByUsername("loginUser")).thenReturn(Optional.of(member))

        // when & then
        val exception = assertThrows(CustomException::class.java) {
            authService.loginMember(LoginRequest("loginUser", rawPassword))
        }

        assertEquals(ErrorCode.AUTH_UNAUTHORIZED, exception.errorCode)
    }
}
