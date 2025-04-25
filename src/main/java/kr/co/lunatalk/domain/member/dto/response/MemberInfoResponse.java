package kr.co.lunatalk.domain.member.dto.response;

import kr.co.lunatalk.domain.member.domain.Member;

public record MemberInfoResponse(Long memberId, String nickname, String profileImgUrl) {
	public static MemberInfoResponse from(Member member) {
		return new MemberInfoResponse(member.getId(), member.getProfile().getNickname(), member.getProfile().getProfileImageUrl());
	}
}
