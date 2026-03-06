package kr.co.lunatalk.domain.product.dto

import kr.co.lunatalk.domain.image.domain.Image
import kr.co.lunatalk.domain.product.domain.Product

data class ProductWithImagesResult(
    val products: List<Product>,
    val imageMap: Map<Long, List<Image>>
)
