package kr.co.lunatalk.domain.category.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import kr.co.lunatalk.domain.category.domain.Category
import kr.co.lunatalk.domain.product.domain.Product

data class CategoryAddProductResponse(
    @Schema(description = "추가한 categoryId")
    val categoryId: Long?,

    @Schema(description = "추가한 productIds")
    val productIds: List<Long?>
) {
    companion object {
        fun of(category: Category, products: List<Product>): CategoryAddProductResponse {
            return CategoryAddProductResponse(
                categoryId = category.id,
                productIds = products.map { it.id }
            )
        }
    }
}
