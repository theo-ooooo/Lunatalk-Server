package kr.co.lunatalk.domain.delivery.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import kr.co.lunatalk.domain.delivery.domain.CourierCompany;
import kr.co.lunatalk.domain.delivery.domain.DeliveryStatus;

public record DeliveryUpdateRequest(
	@Schema(description = "변경할 운송장 번호")
	String trackingNumber,
	@Schema(description = "변경할 택배 회사")
	CourierCompany courierCompany,
	@Schema(description = "변경할 상태")
	DeliveryStatus status
) {
}
