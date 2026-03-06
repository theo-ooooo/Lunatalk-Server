package kr.co.lunatalk.domain.cartitem.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import kr.co.lunatalk.domain.cartitem.domain.CartItem
import kr.co.lunatalk.domain.product.dto.FindProductDto

data class CartFindResponse(
    @field:Schema(description = "cartItem 고유 ID")
    val cartItemId: Long?,

    @field:Schema(description = "상품")
    val product: FindProductDto,

    @field:Schema(description = "상품 갯수")
    val quantity: Int
) {
    companion object {
        fun of(cartItem: CartItem, product: FindProductDto): CartFindResponse {
            return CartFindResponse(cartItem.id, product, cartItem.quantity)
        }
    }
}
