package kr.co.lunatalk.domain.payment.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentStatus {
	READY("준비"),
	DONE("성공"),
	CANCELED("취소")
	;
	private String value;
}

