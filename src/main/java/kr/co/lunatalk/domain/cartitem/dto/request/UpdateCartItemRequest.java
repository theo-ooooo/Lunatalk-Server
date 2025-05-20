package kr.co.lunatalk.domain.cartitem.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateCartItemRequest(
	@Schema(description = "수정할 개수")
	@NotNull(message = "수정할 상품 개수를 전달해주세요.")
	@Min(value = 1, message = "수정할 상품 개수는 1 이상이어야 합니다.")
	Integer quantity
){
}
