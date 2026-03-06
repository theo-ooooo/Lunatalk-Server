package kr.co.lunatalk.domain.auth.dto

import kr.co.lunatalk.domain.member.domain.MemberRole

data class RefreshTokenDto(
    val memberId: Long,
    val memberRole: MemberRole,
    val ttl: Long,
)
