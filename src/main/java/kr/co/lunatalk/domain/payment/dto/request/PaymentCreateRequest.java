package kr.co.lunatalk.domain.payment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import kr.co.lunatalk.domain.payment.domain.PaymentMethod;

public record PaymentCreateRequest(
	@NotBlank(message = "주문 고유 ID를 입력해주세요,")
	@Schema(description = "주문 고유 ID")
	Long orderId,

	@NotBlank(message = "결제수단을 선택해주세요.")
	@Schema(description = "결제 수단")
	PaymentMethod method
) {
}
