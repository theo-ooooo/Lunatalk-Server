package kr.co.lunatalk.domain.product.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import kr.co.lunatalk.domain.product.domain.Product

data class ProductCreateResponse(
    @Schema(description = "상품 ID")
    val productId: Long?
) {
    companion object {
        fun from(product: Product): ProductCreateResponse {
            return ProductCreateResponse(product.id)
        }
    }
}
