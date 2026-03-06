package kr.co.lunatalk.domain.auth.domain

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.TimeToLive

@RedisHash(value = "refreshToken")
class RefreshToken(
    @Id
    val id: Long,
    val refreshToken: String,
    @TimeToLive
    val ttl: Long,
) {
    companion object {
        fun of(memberId: Long, refreshToken: String, ttl: Long): RefreshToken =
            RefreshToken(id = memberId, refreshToken = refreshToken, ttl = ttl)
    }
}
