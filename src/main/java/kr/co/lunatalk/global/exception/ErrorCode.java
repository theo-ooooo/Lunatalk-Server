package kr.co.lunatalk.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
	// Common
	METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP method 입니다."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류, 관리자에게 문의하세요"),

	// Member
	MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
	MEMBER_EXISTS(HttpStatus.CONFLICT, "존재하는 회원입니다."),

	// Auth
	AUTH_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호를 확인해주세요"),
	AUTH_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "시큐리티 인증 정보를 찾을수 없습니다."),
	AUTH_FAILED(HttpStatus.UNAUTHORIZED, "인증에 실패하였습니다."),
	AUTH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Token이 만료 되었습니다.");


	private HttpStatus httpStatus;
	private String message;
}
