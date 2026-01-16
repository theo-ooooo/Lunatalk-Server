package kr.co.lunatalk.global.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.lunatalk.infra.config.jwt.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CookieUtil {

	private final JwtProperties jwtProperties;
	private final SpringEnvironmentUtil springEnvironmentUtil;

	public void setAccessTokenCookie(HttpServletResponse response, String accessToken) {
		Cookie cookie = new Cookie("accessToken", accessToken);
		cookie.setHttpOnly(true);
		cookie.setSecure(!springEnvironmentUtil.isLocalProfile()); // 로컬에서는 false, dev/prod에서는 true
		cookie.setPath("/");
		cookie.setMaxAge((int) (jwtProperties.getAccessTokenExpirationTime() / 1000)); // 초 단위로 변환
		response.addCookie(cookie);
	}

	public void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
		Cookie cookie = new Cookie("refreshToken", refreshToken);
		cookie.setHttpOnly(true);
		cookie.setSecure(!springEnvironmentUtil.isLocalProfile()); // 로컬에서는 false, dev/prod에서는 true
		cookie.setPath("/");
		cookie.setMaxAge((int) (jwtProperties.getRefreshTokenExpirationTime() / 1000)); // 초 단위로 변환
		response.addCookie(cookie);
	}

	public void deleteAccessTokenCookie(HttpServletResponse response) {
		Cookie cookie = new Cookie("accessToken", null);
		cookie.setHttpOnly(true);
		cookie.setSecure(!springEnvironmentUtil.isLocalProfile());
		cookie.setPath("/");
		cookie.setMaxAge(0);
		response.addCookie(cookie);
	}

	public void deleteRefreshTokenCookie(HttpServletResponse response) {
		Cookie cookie = new Cookie("refreshToken", null);
		cookie.setHttpOnly(true);
		cookie.setSecure(!springEnvironmentUtil.isLocalProfile());
		cookie.setPath("/");
		cookie.setMaxAge(0);
		response.addCookie(cookie);
	}
}

