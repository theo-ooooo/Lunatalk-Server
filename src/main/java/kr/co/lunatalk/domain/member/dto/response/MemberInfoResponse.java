package kr.co.lunatalk.domain.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.lunatalk.domain.member.domain.Member;

import java.time.LocalDateTime;

public record MemberInfoResponse(
	@Schema(description = "회원 고유 ID")
	Long memberId,
	@Schema(description = "회원 닉네임")
	String nickname,
	@Schema(description = "핸드폰 번호")
	String phone,
	@Schema(description = "이메일 주소")
	String email,
	@Schema(description = "회원 프로필 이미지 URL")
	String profileImgUrl,

	@Schema(description = "가입일")
	LocalDateTime createdAt
) {
	public static MemberInfoResponse from(Member member) {
		return new MemberInfoResponse(
			member.getId(),
			member.getProfile().getNickname(),
			member.getPhone(),
			member.getEmail(),
			member.getProfile().getProfileImageUrl(),
			member.getCreatedAt()
			);
	}
}
