package kr.co.lunatalk.domain.inquiry.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record InquiryUpdateRequest(
	@NotBlank(message = "제목은 필수입니다.")
	@Schema(description = "문의 제목")
	String title,

	@NotBlank(message = "내용은 필수입니다.")
	@Schema(description = "문의 내용")
	String content
) {
}

