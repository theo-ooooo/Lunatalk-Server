package kr.co.lunatalk.global.util

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import kr.co.lunatalk.domain.auth.dto.AccessTokenDto
import kr.co.lunatalk.domain.auth.dto.RefreshTokenDto
import kr.co.lunatalk.domain.member.domain.MemberRole
import kr.co.lunatalk.infra.config.jwt.JwtProperties
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.security.Key
import java.util.Date

@Component
class JwtUtil(
    private val jwtProperties: JwtProperties
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun generateAccessToken(memberId: Long, memberRole: MemberRole): String {
        val issuedAt = Date()
        val expiredAt = Date(issuedAt.time + jwtProperties.accessTokenExpirationTimeMillis())

        return generateToken(memberId, memberRole, issuedAt, expiredAt, getAccessTokenKey())
    }

    fun generateRefreshToken(memberId: Long, memberRole: MemberRole): String {
        val issuedAt = Date()
        val expiredAt = Date(issuedAt.time + jwtProperties.refreshTokenExpirationTimeMillis())

        return generateToken(memberId, memberRole, issuedAt, expiredAt, getRefreshTokenKey())
    }

    @Throws(ExpiredJwtException::class)
    fun parseAccessToken(token: String): AccessTokenDto? {
        return try {
            val claims = getClaims(token, getAccessTokenKey())

            AccessTokenDto(
                memberId = claims.body.subject.toLong(),
                memberRole = MemberRole.valueOf(claims.body.get("role", String::class.java))
            )
        } catch (e: ExpiredJwtException) {
            throw e
        } catch (e: Exception) {
            null
        }
    }

    @Throws(ExpiredJwtException::class)
    fun parseRefreshToken(token: String): RefreshTokenDto? {
        return try {
            val claims = getClaims(token, getRefreshTokenKey())

            RefreshTokenDto(
                memberId = claims.body.subject.toLong(),
                memberRole = MemberRole.valueOf(claims.body.get("role", String::class.java)),
                ttl = jwtProperties.refreshTokenExpirationTime
            )
        } catch (e: ExpiredJwtException) {
            throw e
        } catch (e: Exception) {
            null
        }
    }

    private fun getAccessTokenKey(): Key =
        Keys.hmacShaKeyFor(jwtProperties.accessTokenSecret.toByteArray())

    private fun getRefreshTokenKey(): Key =
        Keys.hmacShaKeyFor(jwtProperties.refreshTokenSecret.toByteArray())

    private fun getClaims(token: String, key: Key): Jws<Claims> =
        Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)

    fun generateToken(memberId: Long, memberRole: MemberRole, issuedAt: Date, expiredAt: Date, key: Key): String =
        Jwts.builder()
            .setSubject(memberId.toString())
            .claim("role", memberRole.name)
            .setIssuedAt(issuedAt)
            .setExpiration(expiredAt)
            .signWith(key)
            .compact()

    fun getRefreshTokenExpirationTime(): Long = jwtProperties.refreshTokenExpirationTime
}
