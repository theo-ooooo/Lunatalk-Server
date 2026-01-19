package kr.co.lunatalk.domain.order.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {
	ORDER_PENDING("주문대기"),
	ORDERED("주문완료"),
	PAYMENT_COMPLETED("결제 완료"),
	PAYMENT_FAILED("결제 실패"),
	CANCELLED("결제 취소"),
	SHIPPED("배송중"),
	DELIVERED("배송완료"),
	;
	private String value;
}
