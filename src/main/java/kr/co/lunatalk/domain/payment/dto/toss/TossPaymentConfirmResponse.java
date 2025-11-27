package kr.co.lunatalk.domain.payment.dto.toss;

import java.time.LocalDateTime;

public record TossPaymentConfirmResponse(
	String paymentKey,
	String orderId,
	String status,
	Long totalAmount,
	String method,
	LocalDateTime approvedAt
) {
}

