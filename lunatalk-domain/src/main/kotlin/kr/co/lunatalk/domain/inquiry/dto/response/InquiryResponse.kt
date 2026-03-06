package kr.co.lunatalk.domain.inquiry.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import kr.co.lunatalk.domain.inquiry.domain.Inquiry
import kr.co.lunatalk.domain.inquiry.domain.InquiryStatus
import kr.co.lunatalk.domain.inquiry.domain.InquiryType
import kr.co.lunatalk.domain.member.dto.response.MemberInfoResponse
import java.time.LocalDateTime

data class InquiryResponse(
    @field:Schema(description = "문의 ID")
    val inquiryId: Long?,

    @field:Schema(description = "문의 타입")
    val type: InquiryType?,

    @field:Schema(description = "문의 제목")
    val title: String,

    @field:Schema(description = "문의 내용")
    val content: String,

    @field:Schema(description = "문의 상태")
    val status: InquiryStatus,

    @field:Schema(description = "참조 대상 ID (상품 ID, 주문 ID 등, 일반 문의인 경우 null)")
    val referenceId: Long?,

    @field:Schema(description = "참조 대상 이름 (상품 이름, 주문 번호 등)")
    val referenceName: String?,

    @field:Schema(description = "작성자 정보")
    val member: MemberInfoResponse,

    @field:Schema(description = "답변 정보")
    val reply: InquiryReplyResponse?,

    @field:Schema(description = "작성일")
    val createdAt: LocalDateTime?,

    @field:Schema(description = "수정일")
    val updatedAt: LocalDateTime?
) {
    companion object {
        fun from(inquiry: Inquiry, referenceName: String?): InquiryResponse {
            return InquiryResponse(
                inquiryId = inquiry.id,
                type = inquiry.type,
                title = inquiry.title,
                content = inquiry.content,
                status = inquiry.status,
                referenceId = inquiry.referenceId,
                referenceName = referenceName,
                member = MemberInfoResponse.from(inquiry.member!!),
                reply = InquiryReplyResponse.from(inquiry.reply),
                createdAt = inquiry.createdAt,
                updatedAt = inquiry.updatedAt
            )
        }
    }
}
