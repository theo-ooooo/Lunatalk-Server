package kr.co.lunatalk.domain.inquiry.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

data class InquiryUpdateRequest(
    @field:NotBlank(message = "제목은 필수입니다.")
    @field:Schema(description = "문의 제목")
    val title: String? = null,

    @field:NotBlank(message = "내용은 필수입니다.")
    @field:Schema(description = "문의 내용")
    val content: String? = null
)
