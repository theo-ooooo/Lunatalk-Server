package kr.co.lunatalk.domain.inquiry.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.lunatalk.domain.inquiry.domain.Inquiry;
import kr.co.lunatalk.domain.inquiry.domain.InquiryStatus;
import kr.co.lunatalk.domain.inquiry.domain.InquiryType;
import kr.co.lunatalk.domain.member.dto.response.MemberInfoResponse;

import java.time.LocalDateTime;

public record InquiryResponse(
	@Schema(description = "문의 ID")
	Long inquiryId,

	@Schema(description = "문의 타입")
	InquiryType type,

	@Schema(description = "문의 제목")
	String title,

	@Schema(description = "문의 내용")
	String content,

	@Schema(description = "문의 상태")
	InquiryStatus status,

	@Schema(description = "참조 대상 ID (상품 ID, 주문 ID 등, 일반 문의인 경우 null)")
	Long referenceId,

	@Schema(description = "참조 대상 이름 (상품 이름, 주문 번호 등)")
	String referenceName,

	@Schema(description = "작성자 정보")
	MemberInfoResponse member,

	@Schema(description = "답변 정보")
	InquiryReplyResponse reply,

	@Schema(description = "작성일")
	LocalDateTime createdAt,

	@Schema(description = "수정일")
	LocalDateTime updatedAt
) {
	public static InquiryResponse from(Inquiry inquiry, String referenceName) {
		return new InquiryResponse(
			inquiry.getId(),
			inquiry.getType(),
			inquiry.getTitle(),
			inquiry.getContent(),
			inquiry.getStatus(),
			inquiry.getReferenceId(),
			referenceName,
			MemberInfoResponse.from(inquiry.getMember()),
			InquiryReplyResponse.from(inquiry.getReply()),
			inquiry.getCreatedAt(),
			inquiry.getUpdatedAt()
		);
	}
}

