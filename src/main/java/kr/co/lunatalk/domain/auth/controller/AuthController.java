package kr.co.lunatalk.domain.auth.controller;

import jakarta.validation.Valid;
import kr.co.lunatalk.domain.auth.dto.request.LoginRequest;
import kr.co.lunatalk.domain.auth.dto.request.RefreshTokenRequest;
import kr.co.lunatalk.domain.auth.dto.response.AuthTokenResponse;
import kr.co.lunatalk.domain.auth.service.AuthService;
import kr.co.lunatalk.domain.member.dto.request.CreateMemberRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/register")
	public AuthTokenResponse register(@RequestBody @Valid CreateMemberRequest request) {
		return authService.registerMember(request);
	}

	@PostMapping("/login")
	public AuthTokenResponse login(@RequestBody @Valid LoginRequest request) {
		return authService.loginMember(request);
	}

	// 리프레쉬 토큰으로 액세스 토큰 재발급
	@PostMapping("/reissue")
	public AuthTokenResponse reissue(@RequestBody @Valid RefreshTokenRequest request) {
		return authService.reissueTokenPair(request);
	}
}
