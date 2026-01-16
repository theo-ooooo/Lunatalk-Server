package kr.co.lunatalk.domain.inquiry.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.co.lunatalk.domain.inquiry.domain.InquiryType;

public record InquiryCreateRequest(
	@NotNull(message = "문의 타입은 필수입니다.")
	@Schema(description = "문의 타입 (PRODUCT, ORDER, GENERAL)")
	InquiryType type,

	@NotBlank(message = "제목은 필수입니다.")
	@Schema(description = "문의 제목")
	String title,

	@NotBlank(message = "내용은 필수입니다.")
	@Schema(description = "문의 내용")
	String content,

	@Schema(description = "참조 대상 ID (상품 ID 또는 주문 ID, 일반 문의인 경우 생략 가능)")
	Long referenceId,

	@Schema(description = "주문 번호 (주문 문의인 경우, referenceId 대신 사용 가능)")
	String orderNumber
) {
}

