package kr.co.lunatalk.domain.cartitem.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

data class CreateCartItemRequest(
    @field:Schema(description = "상품 ID")
    @field:NotNull(message = "장바구니에 등록할 상품ID를 전달해주세요.")
    val productId: Long? = null,

    @field:Schema(description = "상품 갯수")
    @field:NotNull(message = "장바구니에 등록할 상품 갯수를 전달해주세요.")
    val quantity: Int? = null
)
