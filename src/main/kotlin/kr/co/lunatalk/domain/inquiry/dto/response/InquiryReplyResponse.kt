package kr.co.lunatalk.domain.inquiry.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import kr.co.lunatalk.domain.inquiry.domain.InquiryReply
import kr.co.lunatalk.domain.member.dto.response.MemberInfoResponse
import java.time.LocalDateTime

data class InquiryReplyResponse(
    @field:Schema(description = "답변 ID")
    val replyId: Long?,

    @field:Schema(description = "답변 내용")
    val content: String,

    @field:Schema(description = "답변 작성자 (관리자)")
    val admin: MemberInfoResponse,

    @field:Schema(description = "답변 작성일")
    val createdAt: LocalDateTime?,

    @field:Schema(description = "답변 수정일")
    val updatedAt: LocalDateTime?
) {
    companion object {
        fun from(reply: InquiryReply?): InquiryReplyResponse? {
            if (reply == null) {
                return null
            }
            return InquiryReplyResponse(
                replyId = reply.id,
                content = reply.content,
                admin = MemberInfoResponse.from(reply.admin!!),
                createdAt = reply.createdAt,
                updatedAt = reply.updatedAt
            )
        }
    }
}
