package kr.co.lunatalk.global.common.response

import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.MethodParameter
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.http.server.ServletServerHttpResponse
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice

@RestControllerAdvice
class GlobalResponseAdvice : ResponseBodyAdvice<Any> {

    override fun supports(returnType: MethodParameter, converterType: Class<out HttpMessageConverter<*>>): Boolean {
        return !returnType.declaringClass.packageName.contains("springdoc")
    }

    override fun beforeBodyWrite(
        body: Any?,
        returnType: MethodParameter,
        selectedContentType: MediaType,
        selectedConverterType: Class<out HttpMessageConverter<*>>,
        request: ServerHttpRequest,
        response: ServerHttpResponse
    ): Any? {
        val httpResponse: HttpServletResponse = (response as ServletServerHttpResponse).servletResponse

        // Actuator 엔드포인트는 GlobalResponse로 감싸지 않음
        val path = request.uri.path
        if (path.startsWith("/lunatalk-actuator")) {
            return body
        }

        val status = httpResponse.status
        // 유효한 status가 맞는지.
        val resolve = HttpStatus.resolve(status) ?: return body

        if (body is String) {
            return body
        }

        if (resolve.is2xxSuccessful) {
            return GlobalResponse.success(status, body)
        }

        return body
    }
}
