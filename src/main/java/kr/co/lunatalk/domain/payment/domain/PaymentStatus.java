package kr.co.lunatalk.domain.payment.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentStatus {
	READY("결제 대기"),
	SUCCESS("결제 성공"),
	FAILED("결제 실패"),
	CANCELLED("결제 취소");

	private final String description;
}


