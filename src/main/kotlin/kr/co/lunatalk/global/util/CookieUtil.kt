package kr.co.lunatalk.global.util

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import kr.co.lunatalk.infra.config.jwt.JwtProperties
import org.springframework.stereotype.Component

@Component
class CookieUtil(
    private val jwtProperties: JwtProperties,
    private val springEnvironmentUtil: SpringEnvironmentUtil
) {

    fun setAccessTokenCookie(response: HttpServletResponse, accessToken: String) {
        val cookie = Cookie("accessToken", accessToken).apply {
            isHttpOnly = true
            secure = !springEnvironmentUtil.isLocalProfile() // 로컬에서는 false, dev/prod에서는 true
            path = "/"
            maxAge = (jwtProperties.accessTokenExpirationTimeMillis() / 1000).toInt()
        }
        response.addCookie(cookie)
    }

    fun setRefreshTokenCookie(response: HttpServletResponse, refreshToken: String) {
        val cookie = Cookie("refreshToken", refreshToken).apply {
            isHttpOnly = true
            secure = !springEnvironmentUtil.isLocalProfile() // 로컬에서는 false, dev/prod에서는 true
            path = "/"
            maxAge = (jwtProperties.refreshTokenExpirationTimeMillis() / 1000).toInt()
        }
        response.addCookie(cookie)
    }

    fun deleteAccessTokenCookie(response: HttpServletResponse) {
        val cookie = Cookie("accessToken", null).apply {
            isHttpOnly = true
            secure = !springEnvironmentUtil.isLocalProfile()
            path = "/"
            maxAge = 0
        }
        response.addCookie(cookie)
    }

    fun deleteRefreshTokenCookie(response: HttpServletResponse) {
        val cookie = Cookie("refreshToken", null).apply {
            isHttpOnly = true
            secure = !springEnvironmentUtil.isLocalProfile()
            path = "/"
            maxAge = 0
        }
        response.addCookie(cookie)
    }
}
