package kr.co.lunatalk.domain.inquiry.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import kr.co.lunatalk.domain.inquiry.domain.InquiryType

data class InquiryCreateRequest(
    @field:NotNull(message = "문의 타입은 필수입니다.")
    @field:Schema(description = "문의 타입 (PRODUCT, ORDER, GENERAL)")
    val type: InquiryType? = null,

    @field:NotBlank(message = "제목은 필수입니다.")
    @field:Schema(description = "문의 제목")
    val title: String? = null,

    @field:NotBlank(message = "내용은 필수입니다.")
    @field:Schema(description = "문의 내용")
    val content: String? = null,

    @field:Schema(description = "참조 대상 ID (상품 ID 또는 주문 ID, 일반 문의인 경우 생략 가능)")
    val referenceId: Long? = null,

    @field:Schema(description = "주문 번호 (주문 문의인 경우, referenceId 대신 사용 가능)")
    val orderNumber: String? = null
)
