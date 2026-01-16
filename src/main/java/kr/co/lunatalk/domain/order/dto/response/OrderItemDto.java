package kr.co.lunatalk.domain.order.dto.response;


import kr.co.lunatalk.domain.order.domain.OrderItem;

public record OrderItemDto(
	Long productId,
	String productName,
	Long price,
	Integer quantity,
	Long totalPrice,
	String color
) {
	public static OrderItemDto from(OrderItem orderItem) {
		return new OrderItemDto(
			orderItem.getProductId(),
			orderItem.getProductName(),
			orderItem.getPrice(),
			orderItem.getQuantity(),
			orderItem.getTotalPrice(),
			orderItem.getOptionSnapshot().getColor()
		);
	}
}
