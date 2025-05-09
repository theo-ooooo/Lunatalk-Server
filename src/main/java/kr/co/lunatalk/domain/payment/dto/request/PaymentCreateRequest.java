package kr.co.lunatalk.domain.payment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PaymentCreateRequest(
	@NotNull
	@Schema(description = "주문 ID")
	Long orderId,

	@NotNull
	@Size(min = 1)
	@Schema(description = "결제 금액")
	Long amount,

	@Schema(description = "결제 수단")
	@NotNull
	String method
) {
}
