package kr.co.lunatalk.domain.order.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.lunatalk.domain.order.domain.Order;
import kr.co.lunatalk.domain.order.domain.OrderStatus;

import java.util.List;

public record OrderFIndResponse(
	@Schema(description = "주문 ID", defaultValue = "1") Long orderId,
	@Schema(description = "주문 번호", defaultValue = "L3ABCDEFG") String orderNumber,
	@Schema(description = "주문 상태", defaultValue = "ORDERED") OrderStatus status,
	@Schema(description = "주문 총 금액", defaultValue = "1000") Long totalPrice,
	@Schema(description = "구매 상품들")List<OrderItemDto> orderItems
	) {

	public static OrderFIndResponse from(Order order) {
		return new OrderFIndResponse(
			order.getId(),
			order.getOrderNumber(),
			order.getStatus(),
			order.getTotalPrice(),
			order.getOrderItems().stream().map(OrderItemDto::from).toList()
		);
	}
}
