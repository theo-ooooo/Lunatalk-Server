package kr.co.lunatalk.domain.payment.event;

import java.util.List;

public record PaymentCompletedEvent(
	String orderNumber,
	Long orderId,
	Long totalAmount,
	String memberEmail,
	List<PaymentOrderItem> items
) {
	public record PaymentOrderItem(
		Long productId,
		String productName,
		Integer quantity,
		Long price
	) {
	}
}


