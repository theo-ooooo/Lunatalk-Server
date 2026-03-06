package kr.co.lunatalk.domain.category.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import kr.co.lunatalk.domain.category.domain.Category

data class CategoryCreateResponse(
    @Schema(description = "추가한 categoryId")
    val categoryId: Long?
) {
    companion object {
        fun of(category: Category): CategoryCreateResponse {
            return CategoryCreateResponse(category.id)
        }
    }
}
