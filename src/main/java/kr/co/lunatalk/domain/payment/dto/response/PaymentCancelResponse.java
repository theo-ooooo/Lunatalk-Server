package kr.co.lunatalk.domain.payment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.lunatalk.domain.order.domain.Order;
import kr.co.lunatalk.domain.order.domain.OrderStatus;
import kr.co.lunatalk.domain.payment.domain.Payment;
import kr.co.lunatalk.domain.payment.domain.PaymentStatus;

public record PaymentCancelResponse(
	@Schema(description = "주문 번호", example = "L3ABCDEFG")
	String orderNumber,
	@Schema(description = "결제 상태", example = "CANCELLED")
	PaymentStatus paymentStatus,
	@Schema(description = "주문 상태", example = "CANCELLED")
	OrderStatus orderStatus
) {

	public static PaymentCancelResponse of(Order order, Payment payment) {
		return new PaymentCancelResponse(
			order.getOrderNumber(),
			payment.getStatus(),
			order.getStatus()
		);
	}
}


