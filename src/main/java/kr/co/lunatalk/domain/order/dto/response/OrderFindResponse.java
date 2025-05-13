package kr.co.lunatalk.domain.order.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.lunatalk.domain.delivery.dto.response.DeliveryFindResponse;
import kr.co.lunatalk.domain.member.dto.response.MemberInfoResponse;
import kr.co.lunatalk.domain.order.domain.Order;
import kr.co.lunatalk.domain.order.domain.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record OrderFindResponse(
	@Schema(description = "주문 ID", defaultValue = "1") Long orderId,
	@Schema(description = "주문 번호", defaultValue = "L3ABCDEFG") String orderNumber,
	@Schema(description = "주문 상태", defaultValue = "ORDERED") OrderStatus status,
	@Schema(description = "주문 총 금액", defaultValue = "1000") Long totalPrice,
	@Schema(description = "구매 상품들") List<OrderItemResponse> orderItems,
	@Schema(description = "배송 정보") List<DeliveryFindResponse> deliveries,
	@Schema(description = "회원 정보") MemberInfoResponse member,
	@Schema(description = "주문일")LocalDateTime createdAt
	) {

	public static OrderFindResponse from(Order order) {
		return new OrderFindResponse(
			order.getId(),
			order.getOrderNumber(),
			order.getStatus(),
			order.getTotalPrice(),
			order.getOrderItems().stream().map(OrderItemResponse::from).toList(),
			order.getDeliverys().stream().map(DeliveryFindResponse::from).toList(),
			MemberInfoResponse.from(order.getMember()),
			order.getCreatedAt()
		);
	}
}
