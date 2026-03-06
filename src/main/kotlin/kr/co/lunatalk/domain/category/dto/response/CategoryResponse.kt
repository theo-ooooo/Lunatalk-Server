package kr.co.lunatalk.domain.category.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import kr.co.lunatalk.domain.category.domain.Category
import kr.co.lunatalk.domain.category.domain.CategoryStatus
import kr.co.lunatalk.domain.category.domain.CategoryVisibility

data class CategoryResponse(
    @Schema(description = "카테고리 ID")
    val categoryId: Long?,

    @Schema(description = "카테고리 이름")
    val categoryName: String?,

    @Schema(description = "카테고리 상태")
    val status: CategoryStatus?,

    @Schema(description = "카테고리 노출여부")
    val visibility: CategoryVisibility?,

    @Schema(description = "연결된 상품 갯수")
    val productCount: Int
) {
    companion object {
        fun from(category: Category?): CategoryResponse? {
            if (category == null) return null
            return CategoryResponse(
                categoryId = category.id,
                categoryName = category.name,
                status = category.status,
                visibility = category.visibility,
                productCount = category.products.size
            )
        }
    }
}
