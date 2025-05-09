package kr.co.lunatalk.domain.payment.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PaymentStatus {
	REQUESTED("결제 요청됨"),
	SUCCESS("결제 성공"),
	FAILED("결제 실패"),
	CANCELLED("결제 취소됨"),
	REFUNDED("환불 완료");

	private final String value;
}
