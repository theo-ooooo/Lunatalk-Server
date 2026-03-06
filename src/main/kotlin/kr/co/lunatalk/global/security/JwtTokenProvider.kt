package kr.co.lunatalk.global.security

import kr.co.lunatalk.domain.auth.domain.RefreshToken
import kr.co.lunatalk.domain.auth.dto.AccessTokenDto
import kr.co.lunatalk.domain.auth.dto.RefreshTokenDto
import kr.co.lunatalk.domain.auth.dto.response.TokenResponse
import kr.co.lunatalk.domain.auth.repository.RefreshRepository
import kr.co.lunatalk.domain.member.domain.MemberRole
import kr.co.lunatalk.global.util.JwtUtil
import org.springframework.stereotype.Service

@Service
class JwtTokenProvider(
    private val jwtUtil: JwtUtil,
    private val refreshRepository: RefreshRepository
) {

    fun generateTokenPair(memberId: Long, memberRole: MemberRole): TokenResponse {
        val accessToken = createAccessToken(memberId, memberRole)
        val refreshToken = createRefreshToken(memberId, memberRole)

        return TokenResponse.of(accessToken, refreshToken)
    }

    fun createAccessToken(memberId: Long, memberRole: MemberRole): String =
        jwtUtil.generateAccessToken(memberId, memberRole)

    fun createRefreshToken(memberId: Long, memberRole: MemberRole): String {
        val refreshToken = jwtUtil.generateRefreshToken(memberId, memberRole)
        saveRefreshTokenToRedis(memberId, refreshToken, jwtUtil.getRefreshTokenExpirationTime())
        return refreshToken
    }

    fun parseAccessToken(token: String): AccessTokenDto? {
        return try {
            jwtUtil.parseAccessToken(token)
        } catch (e: Exception) {
            null
        }
    }

    fun parseRefreshToken(token: String): RefreshTokenDto? {
        return try {
            jwtUtil.parseRefreshToken(token)
        } catch (e: Exception) {
            null
        }
    }

    fun retrieveRefreshToken(tokenValue: String): RefreshTokenDto? {
        val refreshTokenDto = parseRefreshToken(tokenValue) ?: return null

        val optionalRefreshToken = getRefreshTokenFromRedis(refreshTokenDto.memberId)

        if (optionalRefreshToken.isEmpty) {
            return null
        }
        val refreshToken = optionalRefreshToken.get()

        if (refreshToken.refreshToken != tokenValue) {
            return null
        }
        return refreshTokenDto
    }

    private fun getRefreshTokenFromRedis(memberId: Long) =
        refreshRepository.findById(memberId)

    private fun saveRefreshTokenToRedis(memberId: Long, refreshTokenValue: String, ttl: Long) {
        val refreshToken = RefreshToken.of(memberId, refreshTokenValue, ttl)
        refreshRepository.save(refreshToken)
    }

    fun deleteRefreshTokenFromRedis(memberId: Long) {
        refreshRepository.deleteById(memberId)
    }
}
