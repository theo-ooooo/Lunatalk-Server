package kr.co.lunatalk.global.exception;

import kr.co.lunatalk.global.common.response.ErrorResponse;
import kr.co.lunatalk.global.common.response.GlobalResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	//	CustomException 에러
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<GlobalResponse> handleCustomException(CustomException e) {
		log.error("CustomException : {}", e);
		ErrorCode errorCode = e.getErrorCode();

		ErrorResponse errorResponse = ErrorResponse.of(errorCode.name(), errorCode.getMessage());
		GlobalResponse response = GlobalResponse.fail(errorCode.getHttpStatus().value(), errorResponse);
		return ResponseEntity.status(errorCode.getHttpStatus().value()).body(response);
	}

	// 이외 에러
	@ExceptionHandler(Exception.class)
	public ResponseEntity<GlobalResponse> handleException(Exception e) {
		ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

		ErrorResponse errorResponse = ErrorResponse.of(e.getClass().getName(), e.getMessage());
		GlobalResponse response = GlobalResponse.fail(errorCode.getHttpStatus().value(), errorResponse);
		return ResponseEntity.status(errorCode.getHttpStatus().value()).body(response);
	}
}
