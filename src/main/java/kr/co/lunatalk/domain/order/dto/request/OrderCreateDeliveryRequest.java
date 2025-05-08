package kr.co.lunatalk.domain.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record OrderCreateDeliveryRequest(
	@NotNull(message = "주소를 입력해주세요.")
	@Schema(description = "주소1")
	String address1,
	@NotNull(message = "주소를 입력해주세요.")
	@Schema(description = "주소2")
	String address2,
	@NotNull(message = "핸드폰 번호를 입력해주세요.")
	@Schema(description = "핸드폰 번호")
	String phoneNumber,
	@NotNull(message = "우편번호를 입력해주세요.")
	@Schema(description = "우편번호")
	String zipCode,
	@NotNull(message = "이름을 입력해주세요.")
	@Schema(description = "이름")
	String name,
	@Schema(description = "배송 메세지")
	String message
) {

}
