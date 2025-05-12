package kr.co.lunatalk.domain.order.dto.response;

import kr.co.lunatalk.domain.order.domain.OrderItem;

public record OrderItemResponse(
	String productName,
	Long price,
	Integer quantity,
	Long totalPrice,
	String color
) {
	public static OrderItemResponse from(OrderItem item) {
		return new OrderItemResponse(
			item.getProductName(),
			item.getPrice(),
			item.getQuantity(),
			item.getTotalPrice(),
			item.getOptionSnapshot().getColor()
		);
	}
}

