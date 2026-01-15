package kr.co.lunatalk.domain.order.dto.response;

import kr.co.lunatalk.domain.order.domain.OrderItem;

public record OrderItemResponse(
	Long productId,
	String productName,
	Long price,
	Integer quantity,
	Long totalPrice,
	String color,
	String productImageUrl
) {
	public static OrderItemResponse from(OrderItem item) {
		return new OrderItemResponse(
			item.getProductId(),
			item.getProductName(),
			item.getPrice(),
			item.getQuantity(),
			item.getTotalPrice(),
			item.getOptionSnapshot().getColor(),
			null
		);
	}

	public static OrderItemResponse from(OrderItem item, String productImageUrl) {
		return new OrderItemResponse(
			item.getProductId(),
			item.getProductName(),
			item.getPrice(),
			item.getQuantity(),
			item.getTotalPrice(),
			item.getOptionSnapshot().getColor(),
			productImageUrl
		);
	}
}

