package kr.co.lunatalk.domain.product.dto

import kr.co.lunatalk.domain.category.dto.response.CategoryResponse
import kr.co.lunatalk.domain.image.domain.Image
import kr.co.lunatalk.domain.product.domain.Product
import kr.co.lunatalk.domain.product.domain.ProductVisibility
import kr.co.lunatalk.global.exception.CustomException
import kr.co.lunatalk.global.exception.ErrorCode

data class FindProductDto(
    val productId: Long?,
    val productName: String?,
    val price: Long?,
    val quantity: Int?,
    val visibility: ProductVisibility?,
    val colors: List<String>,
    val category: CategoryResponse?,
    val images: List<Image>,
    val likeCount: Long,
    val isLiked: Boolean
) {
    companion object {
        fun from(product: Product?, images: List<Image>?, likeCount: Long?, isLiked: Boolean?): FindProductDto {
            if (product == null) {
                throw CustomException(ErrorCode.PRODUCT_NOT_FOUND)
            }
            val safeImages = images ?: emptyList()

            return FindProductDto(
                productId = product.id,
                productName = product.name,
                price = product.price,
                quantity = product.quantity,
                visibility = product.visibility,
                colors = product.productColor.mapNotNull { it.color },
                category = product.category?.let { CategoryResponse.from(it) },
                images = safeImages,
                likeCount = likeCount ?: 0L,
                isLiked = isLiked ?: false
            )
        }
    }
}
