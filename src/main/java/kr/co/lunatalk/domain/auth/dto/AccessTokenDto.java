package kr.co.lunatalk.domain.auth.dto;

import kr.co.lunatalk.domain.member.domain.MemberRole;

public record AccessTokenDto(Long memberId, MemberRole memberRole) {
}
