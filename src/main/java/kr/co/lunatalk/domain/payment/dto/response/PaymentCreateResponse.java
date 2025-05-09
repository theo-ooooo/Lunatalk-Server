package kr.co.lunatalk.domain.payment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record PaymentCreateResponse(
	@Schema(description = "결제 고유 ID") Long paymentId
) {

	public static PaymentCreateResponse of(Long paymentId) {
		return new PaymentCreateResponse(paymentId);
	}
}
