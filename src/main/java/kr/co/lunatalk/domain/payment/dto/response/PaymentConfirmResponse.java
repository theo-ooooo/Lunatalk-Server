package kr.co.lunatalk.domain.payment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.lunatalk.domain.order.domain.Order;
import kr.co.lunatalk.domain.order.domain.OrderStatus;
import kr.co.lunatalk.domain.payment.domain.Payment;
import kr.co.lunatalk.domain.payment.domain.PaymentStatus;

import java.time.LocalDateTime;

public record PaymentConfirmResponse(
	@Schema(description = "주문 번호", example = "L3ABCDEFG")
	String orderNumber,
	@Schema(description = "결제 금액", example = "10000")
	Long amount,
	@Schema(description = "결제 상태", example = "SUCCESS")
	PaymentStatus paymentStatus,
	@Schema(description = "주문 상태", example = "PAYMENT_COMPLETED")
	OrderStatus orderStatus,
	@Schema(description = "토스페이먼츠 paymentKey", example = "pay_2025...")
	String paymentKey,
	@Schema(description = "결제 승인 일시")
	LocalDateTime approvedAt
) {

	public static PaymentConfirmResponse of(Order order, Payment payment) {
		return new PaymentConfirmResponse(
			order.getOrderNumber(),
			payment.getAmount(),
			payment.getStatus(),
			order.getStatus(),
			payment.getPaymentKey(),
			payment.getApprovedAt()
		);
	}
}


