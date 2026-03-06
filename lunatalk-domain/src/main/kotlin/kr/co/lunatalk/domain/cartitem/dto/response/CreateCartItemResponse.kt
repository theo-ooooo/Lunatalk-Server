package kr.co.lunatalk.domain.cartitem.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import kr.co.lunatalk.domain.cartitem.domain.CartItem

data class CreateCartItemResponse(
    @field:Schema(description = "등록된 cartItemId")
    val cartItemId: Long?
) {
    companion object {
        fun from(cartItem: CartItem): CreateCartItemResponse {
            return CreateCartItemResponse(cartItem.id)
        }
    }
}
