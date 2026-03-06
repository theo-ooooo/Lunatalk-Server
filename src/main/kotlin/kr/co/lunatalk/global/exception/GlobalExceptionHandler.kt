package kr.co.lunatalk.global.exception

import kr.co.lunatalk.global.common.response.ErrorResponse
import kr.co.lunatalk.global.common.response.GlobalResponse
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.authorization.AuthorizationDeniedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(e: HttpMessageNotReadableException): ResponseEntity<GlobalResponse> {
        log.error("HttpMessageNotReadableException: {}", e.message)

        val errorCode = ErrorCode.BAD_REQUEST
        val errorResponse = ErrorResponse.of("HttpMessageNotReadableException", errorCode.message)
        val response = GlobalResponse.fail(errorCode.httpStatus.value(), errorResponse)

        return ResponseEntity.status(errorCode.httpStatus).body(response)
    }

    // MethodArgumentNotValidException 발생 시
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(ex: MethodArgumentNotValidException): ResponseEntity<GlobalResponse> {
        log.error("MethodArgumentNotValidException : {}", ex.message)

        val errorCode = ErrorCode.BAD_REQUEST

        val errorResponse = ErrorResponse.of(
            ex.javaClass.simpleName,
            ex.bindingResult.fieldError?.defaultMessage ?: errorCode.message
        )

        val response = GlobalResponse.fail(errorCode.httpStatus.value(), errorResponse)

        return ResponseEntity.status(errorCode.httpStatus).body(response)
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleHttpRequestMethodNotSupportedException(ex: HttpRequestMethodNotSupportedException): ResponseEntity<GlobalResponse> {
        log.error("HttpRequestMethodNotSupportedException : {}", ex.message)

        val errorCode = ErrorCode.METHOD_NOT_ALLOWED

        val errorResponse = ErrorResponse.of(ex.javaClass.simpleName, errorCode.message)

        val response = GlobalResponse.fail(errorCode.httpStatus.value(), errorResponse)
        return ResponseEntity.status(errorCode.httpStatus).body(response)
    }

    @ExceptionHandler(AuthorizationDeniedException::class)
    fun handleAccessDeniedException(ex: AuthorizationDeniedException): ResponseEntity<GlobalResponse> {
        log.error("AuthorizationDeniedException : {}", ex.message)

        val errorCode = ErrorCode.FORBIDDEN
        val errorResponse = ErrorResponse.of(ex.javaClass.simpleName, errorCode.message)
        val response = GlobalResponse.fail(errorCode.httpStatus.value(), errorResponse)
        return ResponseEntity.status(errorCode.httpStatus).body(response)
    }

    // CustomException
    @ExceptionHandler(CustomException::class)
    fun handleCustomException(ex: CustomException): ResponseEntity<GlobalResponse> {
        log.error("CustomException : {}", ex.message)

        val errorCode = ex.errorCode
        val errorResponse = ErrorResponse.of(errorCode.name, errorCode.message)
        val response = GlobalResponse.fail(errorCode.httpStatus.value(), errorResponse)

        return ResponseEntity.status(errorCode.httpStatus).body(response)
    }

    // 그 외 Exception
    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<GlobalResponse> {
        log.error("Exception : {}", ex.message, ex)

        val errorCode = ErrorCode.INTERNAL_SERVER_ERROR
        val errorResponse = ErrorResponse.of(ex.javaClass.simpleName, ex.message ?: "Unknown error")
        val response = GlobalResponse.fail(errorCode.httpStatus.value(), errorResponse)

        return ResponseEntity.status(errorCode.httpStatus).body(response)
    }
}
