package kr.co.lunatalk.domain.product.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import kr.co.lunatalk.domain.category.dto.response.CategoryResponse
import kr.co.lunatalk.domain.image.dto.FindImageDto
import kr.co.lunatalk.domain.product.domain.ProductVisibility
import kr.co.lunatalk.domain.product.dto.FindProductDto

data class ProductFindResponse(
    @Schema(description = "상품 ID")
    val productId: Long?,

    @Schema(description = "상품 이름")
    val name: String?,

    @Schema(description = "상품 가격")
    val price: Long?,

    @Schema(description = "상품 남은 갯수")
    val quantity: Int?,

    @Schema(description = "상품 노출 여부")
    val visibility: ProductVisibility?,

    @Schema(description = "상품 색상들")
    val colors: List<String>,

    @Schema(description = "상품 이미지")
    val images: List<FindImageDto>,

    @Schema(description = "카테고리 정보")
    val category: CategoryResponse?,

    @Schema(description = "좋아요 개수")
    val likeCount: Long,

    @Schema(description = "현재 사용자가 좋아요를 눌렀는지 여부")
    val isLiked: Boolean
) {
    companion object {
        fun from(findProductDto: FindProductDto): ProductFindResponse {
            val images = findProductDto.images.map { FindImageDto.from(it) }

            return ProductFindResponse(
                productId = findProductDto.productId,
                name = findProductDto.productName,
                price = findProductDto.price,
                quantity = findProductDto.quantity,
                visibility = findProductDto.visibility,
                colors = findProductDto.colors,
                images = images,
                category = findProductDto.category,
                likeCount = findProductDto.likeCount,
                isLiked = findProductDto.isLiked
            )
        }
    }
}
