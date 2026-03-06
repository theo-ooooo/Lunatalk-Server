package kr.co.lunatalk.domain.auth.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import kr.co.lunatalk.domain.auth.dto.request.LoginRequest
import kr.co.lunatalk.domain.auth.dto.request.RefreshTokenRequest
import kr.co.lunatalk.domain.auth.dto.response.AuthTokenResponse
import kr.co.lunatalk.domain.auth.service.AuthService
import kr.co.lunatalk.domain.auth.service.KakaoOAuthService
import kr.co.lunatalk.domain.member.dto.request.CreateMemberRequest
import kr.co.lunatalk.global.exception.CustomException
import kr.co.lunatalk.global.exception.ErrorCode
import kr.co.lunatalk.global.util.CookieUtil
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
@Tag(name = "로그인/회원가입", description = "로그인/회원가입 API")
class AuthController(
    private val authService: AuthService,
    private val kakaoOAuthService: KakaoOAuthService,
    private val cookieUtil: CookieUtil,
) {

    @PostMapping("/register")
    @Operation(summary = "회원가입", description = "일반 회원가입후 토큰을 발급합니다.")
    fun register(
        @RequestBody @Valid request: CreateMemberRequest,
        response: HttpServletResponse,
    ): AuthTokenResponse {
        val tokenResponse = authService.registerMember(request)
        setTokenCookies(response, tokenResponse)
        return tokenResponse
    }

    @PostMapping("/login")
    @Operation(summary = "일반 로그인", description = "로그인 후 토큰을 발급합니다.")
    fun login(
        @RequestBody @Valid request: LoginRequest,
        response: HttpServletResponse,
    ): AuthTokenResponse {
        val tokenResponse = authService.loginMember(request)
        setTokenCookies(response, tokenResponse)
        return tokenResponse
    }

    @PostMapping("/admin/login")
    @Operation(summary = "관리자 로그인", description = "관리자 로그인후 토큰을 발급합니다.")
    fun adminLogin(
        @RequestBody @Valid request: LoginRequest,
        response: HttpServletResponse,
    ): AuthTokenResponse {
        val tokenResponse = authService.loginAdmin(request)
        setTokenCookies(response, tokenResponse)
        return tokenResponse
    }

    // 리프레쉬 토큰으로 액세스 토큰 재발급
    @PostMapping("/reissue")
    @Operation(summary = "리프레쉬 토큰 발급", description = "리프레쉬 토큰을 이용해 새로운 액세스토큰과 리프레쉬 토큰을 발급합니다.")
    fun reissue(
        @RequestBody(required = false) @Valid request: RefreshTokenRequest?,
        @CookieValue(value = "refreshToken", required = false) refreshTokenCookie: String?,
        response: HttpServletResponse,
    ): AuthTokenResponse {
        val refreshToken = when {
            request != null && !request.refreshToken.isBlank() -> request.refreshToken
            !refreshTokenCookie.isNullOrBlank() -> refreshTokenCookie
            else -> {
                // 재발급에 사용할 refreshToken이 없으면, 쿠키가 있다면 정리하고 실패 처리
                cookieUtil.deleteRefreshTokenCookie(response)
                throw CustomException(ErrorCode.AUTH_REFRESH_TOKEN_EXPIRED)
            }
        }

        return try {
            val tokenResponse = authService.reissueTokenPair(RefreshTokenRequest(refreshToken))
            setTokenCookies(response, tokenResponse)
            tokenResponse
        } catch (ex: RuntimeException) {
            // 재발급 실패 시 refreshToken 쿠키 제거
            cookieUtil.deleteRefreshTokenCookie(response)
            throw ex
        }
    }

    @DeleteMapping("/withdraw")
    @Operation(summary = "회원탈퇴", description = "회원탈퇴를 진행합니다.")
    fun withdraw(response: HttpServletResponse): ResponseEntity<Void> {
        authService.withdraw()
        deleteTokenCookies(response)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/kakao/callback")
    @Operation(summary = "카카오 소셜 로그인", description = "카카오 인증 코드를 받아 로그인 후 토큰을 발급합니다.")
    fun kakaoLogin(
        @RequestParam("code") authorizationCode: String,
        response: HttpServletResponse,
    ): AuthTokenResponse {
        val tokenResponse = kakaoOAuthService.login(authorizationCode)
        setTokenCookies(response, tokenResponse)
        return tokenResponse
    }

    private fun setTokenCookies(response: HttpServletResponse, tokenResponse: AuthTokenResponse) {
        cookieUtil.setAccessTokenCookie(response, tokenResponse.accessToken)
        cookieUtil.setRefreshTokenCookie(response, tokenResponse.refreshToken)
    }

    private fun deleteTokenCookies(response: HttpServletResponse) {
        cookieUtil.deleteAccessTokenCookie(response)
        cookieUtil.deleteRefreshTokenCookie(response)
    }
}
