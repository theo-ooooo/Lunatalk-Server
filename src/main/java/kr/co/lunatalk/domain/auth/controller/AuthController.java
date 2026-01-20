package kr.co.lunatalk.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.co.lunatalk.domain.auth.dto.request.LoginRequest;
import kr.co.lunatalk.domain.auth.dto.request.RefreshTokenRequest;
import kr.co.lunatalk.domain.auth.dto.response.AuthTokenResponse;
import kr.co.lunatalk.domain.auth.service.AuthService;
import kr.co.lunatalk.domain.auth.service.KakaoOAuthService;
import kr.co.lunatalk.domain.member.dto.request.CreateMemberRequest;
import kr.co.lunatalk.global.exception.CustomException;
import kr.co.lunatalk.global.exception.ErrorCode;
import kr.co.lunatalk.global.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "로그인/회원가입", description = "로그인/회원가입 API")
public class AuthController {

	private final AuthService authService;
	private final KakaoOAuthService kakaoOAuthService;
	private final CookieUtil cookieUtil;

	@PostMapping("/register")
	@Operation(summary = "회원가입", description = "일반 회원가입후 토큰을 발급합니다.")
	public AuthTokenResponse register(@RequestBody @Valid CreateMemberRequest request, HttpServletResponse response) {
		AuthTokenResponse tokenResponse = authService.registerMember(request);
		setTokenCookies(response, tokenResponse);
		return tokenResponse;
	}

	@PostMapping("/login")
	@Operation(summary = "일반 로그인", description = "로그인 후 토큰을 발급합니다.")
	public AuthTokenResponse login(@RequestBody @Valid LoginRequest request, HttpServletResponse response) {
		AuthTokenResponse tokenResponse = authService.loginMember(request);
		setTokenCookies(response, tokenResponse);
		return tokenResponse;
	}

	@PostMapping("/admin/login")
	@Operation(summary = "관리자 로그인", description = "관리자 로그인후 토큰을 발급합니다.")
	public AuthTokenResponse adminLogin(@RequestBody @Valid LoginRequest request, HttpServletResponse response) {
		AuthTokenResponse tokenResponse = authService.loginAdmin(request);
		setTokenCookies(response, tokenResponse);
		return tokenResponse;
	}

	// 리프레쉬 토큰으로 액세스 토큰 재발급
	@PostMapping("/reissue")
	@Operation(summary = "리프레쉬 토큰 발급", description = "리프레쉬 토큰을 이용해 새로운 액세스토큰과 리프레쉬 토큰을 발급합니다.")
	public AuthTokenResponse reissue(
		@RequestBody(required = false) @Valid RefreshTokenRequest request,
		@CookieValue(value = "refreshToken", required = false) String refreshTokenCookie,
		HttpServletResponse response
	) {
		String refreshToken = null;
		if (request != null && request.refreshToken() != null && !request.refreshToken().isBlank()) {
			refreshToken = request.refreshToken();
		} else if (refreshTokenCookie != null && !refreshTokenCookie.isBlank()) {
			refreshToken = refreshTokenCookie;
		}

		if (refreshToken == null) {
			// 재발급에 사용할 refreshToken이 없으면, 쿠키가 있다면 정리하고 실패 처리
			cookieUtil.deleteRefreshTokenCookie(response);
			throw new CustomException(ErrorCode.AUTH_REFRESH_TOKEN_EXPIRED);
		}

		try {
			AuthTokenResponse tokenResponse = authService.reissueTokenPair(new RefreshTokenRequest(refreshToken));
			setTokenCookies(response, tokenResponse);
			return tokenResponse;
		} catch (RuntimeException ex) {
			// 재발급 실패 시 refreshToken 쿠키 제거
			cookieUtil.deleteRefreshTokenCookie(response);
			throw ex;
		}
	}

	@DeleteMapping("/withdraw")
	@Operation(summary = "회원탈퇴", description = "회원탈퇴를 진행합니다.")
	public ResponseEntity<Void> withdraw(HttpServletResponse response) {
		authService.withdraw();
		deleteTokenCookies(response);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/kakao/callback")
	@Operation(summary = "카카오 소셜 로그인", description = "카카오 인증 코드를 받아 로그인 후 토큰을 발급합니다.")
	public AuthTokenResponse kakaoLogin(
		@RequestParam("code") String authorizationCode,
		HttpServletResponse response
	) {
		AuthTokenResponse tokenResponse = kakaoOAuthService.login(authorizationCode);
		setTokenCookies(response, tokenResponse);
		return tokenResponse;
	}

	private void setTokenCookies(HttpServletResponse response, AuthTokenResponse tokenResponse) {
		cookieUtil.setAccessTokenCookie(response, tokenResponse.accessToken());
		cookieUtil.setRefreshTokenCookie(response, tokenResponse.refreshToken());
	}

	private void deleteTokenCookies(HttpServletResponse response) {
		cookieUtil.deleteAccessTokenCookie(response);
		cookieUtil.deleteRefreshTokenCookie(response);
	}
}
