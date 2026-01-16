package kr.co.lunatalk.domain.order.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record OrderCreateResponse(
	@Schema(name = "회원용_주문번호")
	String orderNumber,
	@Schema(name = "관리자용_주문번호")
	Long orderId) {

	public static OrderCreateResponse of(String orderNumber, Long orderId) {
		return new OrderCreateResponse(orderNumber, orderId);
	}
}
