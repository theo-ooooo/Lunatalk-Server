package kr.co.lunatalk.domain.auth.dto;

import kr.co.lunatalk.domain.member.domain.MemberRole;

public record TokenDto(Long memberId, MemberRole memberRole) {
}
