package kr.co.lunatalk.domain.payment.dto.toss;

public record TossPaymentConfirmRequest(
	String paymentKey,
	String orderId,
	Long amount
) {
}

