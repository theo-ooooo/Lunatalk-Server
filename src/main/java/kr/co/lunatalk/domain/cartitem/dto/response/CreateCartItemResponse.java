package kr.co.lunatalk.domain.cartitem.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.lunatalk.domain.cartitem.domain.CartItem;

public record CreateCartItemResponse(
	@Schema(description = "등록된 cartItemId")
	Long cartItemId
) {

	public static CreateCartItemResponse from(CartItem cartItem) {
		return new CreateCartItemResponse(cartItem.getId());
	}
}
