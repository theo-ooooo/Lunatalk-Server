package kr.co.lunatalk.domain.payment.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record PaymentWebhookTossRequest(
	String eventType,
	LocalDateTime createdAt,
	Data data
) {
	public record Data(
		@NotBlank
		String mId,

		String version,

		String lastTransactionKey,

		@NotBlank
		String paymentKey,

		@NotBlank
		String orderId,

		String status,

		String requestedAt,

		String approvedAt,

		boolean useEscrow,

		Integer suppliedAmount,

		Integer vat
	) {}
}
