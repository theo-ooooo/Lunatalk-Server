package kr.co.lunatalk.domain.inquiry.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record InquiryReplyCreateRequest(
	@NotBlank(message = "답변 내용은 필수입니다.")
	@Schema(description = "답변 내용")
	String content
) {
}

