package kr.co.lunatalk.domain.category.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import kr.co.lunatalk.domain.product.dto.response.ProductFindResponse

data class CategoryProductResponse(
    @Schema(description = "categoryId")
    val categoryId: Long?,

    @Schema(description = "카테고리 이름")
    val categoryName: String?,

    @Schema(description = "카테고리에 소속된 상품들")
    val products: List<ProductFindResponse>
) {
    companion object {
        fun of(categoryId: Long?, categoryName: String?, products: List<ProductFindResponse>): CategoryProductResponse {
            return CategoryProductResponse(categoryId, categoryName, products)
        }
    }
}
