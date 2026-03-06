package kr.co.lunatalk.domain.exhibition.dto

import io.swagger.v3.oas.annotations.media.Schema
import kr.co.lunatalk.domain.image.domain.Image
import kr.co.lunatalk.domain.product.domain.Product
import kr.co.lunatalk.domain.product.dto.FindProductDto

data class ExhibitionProductDto(
    @field:Schema(description = "상품 정보")
    val product: FindProductDto,

    @field:Schema(description = "기획전 내 정렬 순서")
    val sortOrder: Int
) {
    companion object {
        fun from(product: Product, images: List<Image>, sortOrder: Int, likeCount: Long, isLiked: Boolean): ExhibitionProductDto {
            return ExhibitionProductDto(FindProductDto.from(product, images, likeCount, isLiked), sortOrder)
        }
    }
}
