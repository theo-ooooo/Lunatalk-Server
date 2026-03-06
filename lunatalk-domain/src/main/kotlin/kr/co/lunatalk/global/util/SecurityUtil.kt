package kr.co.lunatalk.global.util

import kr.co.lunatalk.global.exception.CustomException
import kr.co.lunatalk.global.exception.ErrorCode
import kr.co.lunatalk.global.security.PrincipalDetails
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class SecurityUtil {

    fun getCurrentMemberId(): Long {
        val authentication = SecurityContextHolder.getContext().authentication

        val principal = authentication?.principal
        if (principal is PrincipalDetails) {
            return principal.username.toLong()
        }
        throw CustomException(ErrorCode.UNAUTHORIZED)
    }

    fun getCurrentMemberRole(): String {
        val authentication = SecurityContextHolder.getContext().authentication

        return authentication?.authorities
            ?.map { it.authority }
            ?.firstOrNull()
            ?: throw CustomException(ErrorCode.UNAUTHORIZED)
    }
}
