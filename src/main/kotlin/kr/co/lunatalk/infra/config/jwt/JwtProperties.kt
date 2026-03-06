package kr.co.lunatalk.infra.config.jwt

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    val accessTokenSecret: String,
    val refreshTokenSecret: String,
    val accessTokenExpirationTime: Long,
    val refreshTokenExpirationTime: Long,
    val issuer: String
) {
    fun accessTokenExpirationTimeMillis(): Long = accessTokenExpirationTime * 1000

    fun refreshTokenExpirationTimeMillis(): Long = refreshTokenExpirationTime * 1000
}
