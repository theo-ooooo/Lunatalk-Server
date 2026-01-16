package kr.co.lunatalk.domain.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.lunatalk.domain.order.domain.OrderStatus;

public record OrderUpdateRequest(
	@Schema(description = "주문 상태")
	OrderStatus status
) {
}
