package kr.co.lunatalk.domain.payment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.lunatalk.domain.order.dto.response.OrderFindResponse;
import kr.co.lunatalk.domain.payment.domain.Payment;

public record PaymentCreateResponse(
	@Schema(description = "결제 고유 ID")
	Long paymentId,
	@Schema(description = "주문 정보")
	OrderFindResponse order
) {

	public static PaymentCreateResponse from(Payment payment) {
		return new PaymentCreateResponse(payment.getId(), OrderFindResponse.from(payment.getOrder()));
	}
}
