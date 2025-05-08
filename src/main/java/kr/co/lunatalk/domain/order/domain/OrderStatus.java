package kr.co.lunatalk.domain.order.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {
	ORDERED("ORDERED"),
	PAYMENT_FAILED("PAYMENT_FAILED"),
	CANCELLED("CANCELLED"),
	SHIPPED("SHIPPED"),
	DELIVERED("DELIVERED"),
	;
	private String value;
}
