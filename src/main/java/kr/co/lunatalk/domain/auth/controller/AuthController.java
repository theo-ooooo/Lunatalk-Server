package kr.co.lunatalk.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.lunatalk.domain.auth.dto.request.LoginRequest;
import kr.co.lunatalk.domain.auth.dto.request.RefreshTokenRequest;
import kr.co.lunatalk.domain.auth.dto.response.AuthTokenResponse;
import kr.co.lunatalk.domain.auth.service.AuthService;
import kr.co.lunatalk.domain.member.dto.request.CreateMemberRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "로그인/회원가입", description = "로그인/회원가입 API")
public class AuthController {

	private final AuthService authService;

	@PostMapping("/register")
	@Operation(summary = "회원가입", description = "일반 회원가입후 토큰을 발급합니다.")
	public AuthTokenResponse register(@RequestBody @Valid CreateMemberRequest request) {
		return authService.registerMember(request);
	}

	@PostMapping("/login")
	@Operation(summary = "로그인", description = "로그인 후 토큰을 발급합니다.")
	public AuthTokenResponse login(@RequestBody @Valid LoginRequest request) {
		return authService.loginMember(request);
	}

	@PostMapping("/admin/login")
	@Operation(summary = "관리자 로그인", description = "관리자 로그인후 토큰을 발급합니다.")
	public AuthTokenResponse adminLogin(@RequestBody @Valid LoginRequest request) {
		return authService.loginAdmin(request);
	}

	// 리프레쉬 토큰으로 액세스 토큰 재발급
	@PostMapping("/reissue")
	@Operation(summary = "리프레쉬 토큰 발급", description = "리프레쉬 토큰을 이용해 새로운 액세스토큰과 리프레쉬 토큰을 발급합니다.")
	public AuthTokenResponse reissue(@RequestBody @Valid RefreshTokenRequest request) {
		return authService.reissueTokenPair(request);
	}

	@DeleteMapping("/withdraw")
	@Operation(summary = "회원탈퇴", description = "회원탈퇴를 진행합니다.")
	public ResponseEntity<Void> withdraw() {
		authService.withdraw();
		return ResponseEntity.ok().build();
	}
}
