package kr.co.lunatalk.domain.cartitem.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.lunatalk.domain.cartitem.domain.CartItem;
import kr.co.lunatalk.domain.product.dto.FindProductDto;

public record CartFindResponse(
	@Schema(description = "cartItem 고유 ID")
	Long cartItemId,

	@Schema(description = "상품")
	FindProductDto product,

	@Schema(description = "상품 갯수")
	Integer quantity
) {

	public static CartFindResponse of(CartItem cartItem, FindProductDto product) {
		return new CartFindResponse(cartItem.getId(), product, cartItem.getQuantity());
	}
}
