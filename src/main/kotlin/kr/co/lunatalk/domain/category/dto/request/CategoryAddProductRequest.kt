package kr.co.lunatalk.domain.category.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

data class CategoryAddProductRequest(
    @field:NotNull(message = "상품을 선택해주세요.")
    @Schema(description = "추가할 상품 Ids")
    val productIds: List<Long>
)
