package kr.co.lunatalk.domain.cartitem.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateCartItemRequest(
	@Schema(description = "수정할 갯수")
	Integer quantity
){
}
