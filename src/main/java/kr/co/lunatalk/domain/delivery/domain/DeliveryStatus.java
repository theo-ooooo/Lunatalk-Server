package kr.co.lunatalk.domain.delivery.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DeliveryStatus {
	READY("READY"),         // 배송 준비중
	SHIPPED("SHIPPED"),       // 배송 중
	DELIVERED("DELIVERED"),     // 배송 완료
	RETURNED("RETURNED"),      // 반품
	REDELIVERY("REDELIVERY")     // 재배송
	;
	private String value;
}
