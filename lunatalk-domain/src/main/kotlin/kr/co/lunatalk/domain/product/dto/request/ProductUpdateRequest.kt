package kr.co.lunatalk.domain.product.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import kr.co.lunatalk.domain.product.domain.ProductVisibility

data class ProductUpdateRequest(
    @field:NotBlank(message = "상품 이름은 필수로 입력해야 합니다.")
    @Schema(description = "상품 이름")
    val name: String?,

    @field:NotNull(message = "상품 가격은 필수로 입력해야 합니다.")
    @field:Min(value = 0, message = "상품 가격은 0 이상이여야 합니다.")
    @Schema(description = "상품 가격")
    val price: Long?,

    @field:NotNull(message = "상품 갯수는 필수로 입력해야 합니다.")
    @field:Min(value = 0, message = "상품 갯수는 1개 이상이여야 합니다.")
    @Schema(description = "상품 갯수")
    val quantity: Int?,

    @field:NotNull(message = "상품 노출여부는 필수 입니다.")
    @Schema(description = "상품 노출 여부")
    val visibility: ProductVisibility?,

    @Schema(description = "상품 색상들")
    val colors: List<String> = emptyList(),

    @Schema(description = "연결할 카테고리 고유 ID")
    val categoryId: Long?
)
