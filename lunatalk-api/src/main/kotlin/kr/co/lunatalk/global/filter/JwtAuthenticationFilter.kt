package kr.co.lunatalk.global.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kr.co.lunatalk.domain.auth.dto.AccessTokenDto
import kr.co.lunatalk.global.security.JwtTokenProvider
import kr.co.lunatalk.global.security.PrincipalDetails
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider
) : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = request.getHeader("Authorization")

        if (token != null && token.startsWith("Bearer ")) {
            val jwtToken = token.replaceFirst("Bearer ", "")
            val tokenDto = jwtTokenProvider.parseAccessToken(jwtToken)
            // 토큰 검증 완료후 SecurityContextHolder 내 인증 정보가 없는 경우만 저장
            if (tokenDto != null && SecurityContextHolder.getContext().authentication == null) {
                setAuthenticationToContext(tokenDto)
            }
        }
        filterChain.doFilter(request, response)
    }

    companion object {
        private fun setAuthenticationToContext(tokenDto: AccessTokenDto) {
            val userDetails = PrincipalDetails(tokenDto.memberId, tokenDto.memberRole)
            val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)

            SecurityContextHolder.getContext().authentication = authentication
        }
    }
}
