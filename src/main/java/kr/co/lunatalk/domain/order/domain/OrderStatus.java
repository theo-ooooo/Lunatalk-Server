package kr.co.lunatalk.domain.order.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {

	CREATED("주문 생성"),
	PAYMENT_PENDING("결제 대기중"),
	PAID("결제 완료"),
	PAYMENT_FAILED("결제 실패"),
	CANCELLED("주문 취소"),
	SHIPPED("배송 중"),
	DELIVERED("배송 완료");

	private final String value;
}

