package kr.co.lunatalk.global.config.security

import tools.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kr.co.lunatalk.global.common.response.ErrorResponse
import kr.co.lunatalk.global.common.response.GlobalResponse
import kr.co.lunatalk.global.exception.ErrorCode
import kr.co.lunatalk.global.util.CookieUtil
import org.springframework.context.annotation.Lazy
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component

@Component
class CustomAccessDeniedHandler(
    @Lazy private val mapper: ObjectMapper,
    @Lazy private val cookieUtil: CookieUtil
) : AccessDeniedHandler {

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        cookieUtil.deleteAccessTokenCookie(response)

        response.contentType = "application/json;charset=UTF-8"
        response.status = HttpStatus.FORBIDDEN.value()
        val errorResponse = GlobalResponse.fail(
            ErrorCode.AUTH_FAILED.httpStatus.value(),
            ErrorResponse(ErrorCode.AUTH_FAILED.name, ErrorCode.AUTH_FAILED.message)
        )
        response.writer.write(mapper.writeValueAsString(errorResponse))
    }
}
