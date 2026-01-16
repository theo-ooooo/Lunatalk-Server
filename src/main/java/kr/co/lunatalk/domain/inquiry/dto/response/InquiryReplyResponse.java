package kr.co.lunatalk.domain.inquiry.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.lunatalk.domain.inquiry.domain.InquiryReply;
import kr.co.lunatalk.domain.member.dto.response.MemberInfoResponse;

import java.time.LocalDateTime;

public record InquiryReplyResponse(
	@Schema(description = "답변 ID")
	Long replyId,

	@Schema(description = "답변 내용")
	String content,

	@Schema(description = "답변 작성자 (관리자)")
	MemberInfoResponse admin,

	@Schema(description = "답변 작성일")
	LocalDateTime createdAt,

	@Schema(description = "답변 수정일")
	LocalDateTime updatedAt
) {
	public static InquiryReplyResponse from(InquiryReply reply) {
		if (reply == null) {
			return null;
		}
		return new InquiryReplyResponse(
			reply.getId(),
			reply.getContent(),
			MemberInfoResponse.from(reply.getAdmin()),
			reply.getCreatedAt(),
			reply.getUpdatedAt()
		);
	}
}

