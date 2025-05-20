package kr.co.lunatalk.domain.delivery.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.lunatalk.domain.delivery.domain.CourierCompany;
import kr.co.lunatalk.domain.delivery.domain.Delivery;
import kr.co.lunatalk.domain.delivery.domain.DeliveryStatus;

public record DeliveryFindResponse(
	@Schema(description = "배송 고유 ID")
	Long deliveryId,
	@Schema(description = "수취인 이름")
	String receiverName,
	@Schema(description = "수취인 휴대폰 번호")
	String receiverPhone,

	@Schema(description = "주소1")
	String addressLine1,
	@Schema(description = "주소2")
	String addressLine2,
	@Schema(description = "우편번호")
	String zipcode,
	@Schema(description = "배송 메세지")
	String message,

	@Schema(description = "택배 회사")
	CourierCompany courierCompany,
	@Schema(description = "운송장 번호")
	String trackingNumber,

	@Schema(description = "배송 상태")
	DeliveryStatus status

	) {

	public static DeliveryFindResponse from(Delivery delivery) {
		return new DeliveryFindResponse(
			delivery.getId(),
			delivery.getReceiverName(),
			delivery.getReceiverPhone(),
			delivery.getAddressLine1(),
			delivery.getAddressLine2(),
			delivery.getZipcode(),
			delivery.getMessage(),
			delivery.getCourierCompany(),
			delivery.getTrackingNumber(),
			delivery.getStatus());
	}
}
