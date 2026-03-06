package kr.co.lunatalk.domain.cartitem.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class UpdateCartItemRequest(
    @field:Schema(description = "수정할 개수")
    @field:NotNull(message = "수정할 상품 개수를 전달해주세요.")
    @field:Min(value = 1, message = "수정할 상품 개수는 1 이상이어야 합니다.")
    val quantity: Int? = null
)
