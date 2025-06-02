package kr.co.lunatalk.domain.payment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record PaymentTossConfirmRequest(
	@NotBlank(message = "결제 타입을 정해주세요.")
	@Schema(description = "결제 타입")
	String paymentType,

	@NotBlank(message = "주문 고유 ID를 보내주세요.")
	@Schema(description = "주문 고유 ID")
	Long orderId,

	@NotBlank(message = "결제 고유 키를 보내주세요.")
	@Schema(description = "결제 키")
	String paymentKey,

	@NotBlank(message = "결제 금액을 입력해주세요.")
	@Schema(description = "결제 된 금액")
	Long amount
) {
}
