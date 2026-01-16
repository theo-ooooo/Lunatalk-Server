package kr.co.lunatalk.domain.delivery.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CourierCompany {
	CJ_LOGISTICS("CJ대한통운"),
	HANJIN("한진택배"),
	LOTTE("롯데택배"),
	KOREA_POST("우체국택배"),
	LOGEN("로젠택배"),
	UPS("UPS"),
	DHL("DHL"),
	FEDEX("FedEx"),
	OTHER("기타");

	private final String displayName;
}
