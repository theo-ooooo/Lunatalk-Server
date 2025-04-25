package kr.co.lunatalk.global.common.response;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class GlobalResponseAdvice implements ResponseBodyAdvice<Object> {
	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}

	@Override
	public Object beforeBodyWrite(
		Object body,
		MethodParameter returnType,
		MediaType selectedContentType,
		Class<? extends HttpMessageConverter<?>> selectedConverterType,
		ServerHttpRequest request,
		ServerHttpResponse response) {
		HttpServletResponse httpResponse = ((ServletServerHttpResponse) response).getServletResponse();

		int status = httpResponse.getStatus();
		// 유효한 status가 맞는지.
		HttpStatus resolve = HttpStatus.resolve(status);

		if (resolve == null || body instanceof String) {
			return body;
		}

		if (resolve.is2xxSuccessful()) {
			return GlobalResponse.success(status, body);
		}

		return body;
	}
}
