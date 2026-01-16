package kr.co.lunatalk.domain.auth.dto;

import kr.co.lunatalk.domain.member.domain.MemberRole;

public record RefreshTokenDto(Long memberId, MemberRole memberRole, Long ttl) {
}
