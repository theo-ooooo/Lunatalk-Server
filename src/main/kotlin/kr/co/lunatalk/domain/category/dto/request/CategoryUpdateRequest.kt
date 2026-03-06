package kr.co.lunatalk.domain.category.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import kr.co.lunatalk.domain.category.domain.CategoryVisibility

data class CategoryUpdateRequest(
    @field:NotNull(message = "변경할 이름을 작성해 주세요.")
    @Schema(description = "변경할 카테고리 이름")
    val name: String,

    @field:NotNull(message = "변경할 노출여부를 작성해 주세요.")
    @Schema(description = "변경할 노출 여부")
    val visibility: CategoryVisibility
)
