package kr.co.lunatalk.global.exception;

import kr.co.lunatalk.global.common.response.ErrorResponse;
import kr.co.lunatalk.global.common.response.GlobalResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	// MethodArgumentNotValidException 발생 시
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<GlobalResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
		log.error("MethodArgumentNotValidException : {}", ex.getMessage());

		ErrorCode errorCode = ErrorCode.BAD_REQUEST;

		ErrorResponse errorResponse = ErrorResponse.of(
			ex.getClass().getSimpleName(),
			ex.getBindingResult().getFieldError().getDefaultMessage()
		);

		GlobalResponse response = GlobalResponse.fail(errorCode.getHttpStatus().value(), errorResponse);

		return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<GlobalResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
		log.error("HttpRequestMethodNotSupportedException : {}", ex.getMessage());

		ErrorCode errorCode = ErrorCode.METHOD_NOT_ALLOWED;

		ErrorResponse errorResponse = ErrorResponse.of(ex.getClass().getSimpleName(), errorCode.getMessage());

		GlobalResponse response = GlobalResponse.fail(errorCode.getHttpStatus().value(), errorResponse);
		return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
	}

	// CustomException
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<GlobalResponse> handleCustomException(CustomException ex) {
		log.error("CustomException : {}", ex.getMessage());

		ErrorCode errorCode = ex.getErrorCode();
		ErrorResponse errorResponse = ErrorResponse.of(errorCode.name(), errorCode.getMessage());
		GlobalResponse response = GlobalResponse.fail(errorCode.getHttpStatus().value(), errorResponse);

		return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
	}

	// 그 외 Exception
	@ExceptionHandler(Exception.class)
	public ResponseEntity<GlobalResponse> handleException(Exception ex) {
		log.error("Exception : {}", ex.getMessage(), ex);

		ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
		ErrorResponse errorResponse = ErrorResponse.of(ex.getClass().getSimpleName(), ex.getMessage());
		GlobalResponse response = GlobalResponse.fail(errorCode.getHttpStatus().value(), errorResponse);

		return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
	}
}
