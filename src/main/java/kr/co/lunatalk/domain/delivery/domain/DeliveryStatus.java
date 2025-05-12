package kr.co.lunatalk.domain.delivery.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DeliveryStatus {
	READY("배송 준비중"),         // 배송 준비중
	SHIPPED("배송중"),       // 배송 중
	DELIVERED("배송 완료"),     // 배송 완료
	RETURNED("반품"),      // 반품
	REDELIVERY("재배송")     // 재배송
	;
	private String value;
}
