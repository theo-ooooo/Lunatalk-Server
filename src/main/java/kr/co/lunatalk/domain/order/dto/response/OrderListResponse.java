package kr.co.lunatalk.domain.order.dto.response;

import kr.co.lunatalk.domain.order.domain.Order;

import java.time.LocalDateTime;
import java.util.List;

public record OrderListResponse(
	Long orderId,
	String orderNumber,
	String status,
	Long totalPrice,
	LocalDateTime createdAt,
	String nickname,
	List<OrderItemResponse> orderItems // 여기서 그냥 getter 사용하면 됨
) {
	public static OrderListResponse from(Order order) {
		return new OrderListResponse(
			order.getId(),
			order.getOrderNumber(),
			order.getStatus().getValue(),
			order.getTotalPrice(),
			order.getCreatedAt(),
			order.getMember().getProfile().getNickname(),
			order.getOrderItems().stream().map(OrderItemResponse::from).toList()
		);
	}
}
