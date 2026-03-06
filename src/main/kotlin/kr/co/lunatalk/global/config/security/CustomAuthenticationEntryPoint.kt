package kr.co.lunatalk.global.config.security

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kr.co.lunatalk.global.common.response.ErrorResponse
import kr.co.lunatalk.global.common.response.GlobalResponse
import kr.co.lunatalk.global.exception.ErrorCode
import kr.co.lunatalk.global.util.CookieUtil
import org.springframework.context.annotation.Lazy
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationEntryPoint(
    @Lazy private val mapper: ObjectMapper,
    @Lazy private val cookieUtil: CookieUtil
) : AuthenticationEntryPoint {

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        cookieUtil.deleteAccessTokenCookie(response)

        response.contentType = "application/json;charset=UTF-8"
        response.status = HttpStatus.UNAUTHORIZED.value()
        val errorResponse = GlobalResponse.fail(
            ErrorCode.AUTH_TOKEN_EXPIRED.httpStatus.value(),
            ErrorResponse(ErrorCode.AUTH_TOKEN_EXPIRED.name, ErrorCode.AUTH_TOKEN_EXPIRED.message)
        )
        response.writer.write(mapper.writeValueAsString(errorResponse))
    }
}
