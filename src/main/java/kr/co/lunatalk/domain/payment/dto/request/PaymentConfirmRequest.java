package kr.co.lunatalk.domain.payment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record PaymentConfirmRequest(
	@Schema(description = "토스페이먼츠에서 전달받은 paymentKey", example = "pay_2025...")
	String paymentKey,
	@Schema(description = "주문 번호 (Toss orderId, 서버의 orderNumber와 동일)", example = "L3ABCDEFG")
	String orderId,
	@Schema(description = "결제 금액(총 주문 금액과 일치해야 함)", example = "10000")
	Long amount
) {
}


