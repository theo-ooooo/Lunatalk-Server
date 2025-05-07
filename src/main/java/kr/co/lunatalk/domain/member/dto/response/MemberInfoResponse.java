package kr.co.lunatalk.domain.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.lunatalk.domain.member.domain.Member;

public record MemberInfoResponse(
	@Schema(description = "회원 고유 ID")
	Long memberId,
	@Schema(description = "회원 닉네임")
	String nickname,
	@Schema(description = "회원 프로필 이미지 URL")
	String profileImgUrl) {
	public static MemberInfoResponse from(Member member) {
		return new MemberInfoResponse(member.getId(), member.getProfile().getNickname(), member.getProfile().getProfileImageUrl());
	}
}
