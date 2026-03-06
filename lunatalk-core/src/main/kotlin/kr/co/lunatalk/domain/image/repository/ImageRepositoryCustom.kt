package kr.co.lunatalk.domain.image.repository

import kr.co.lunatalk.domain.image.domain.Image
import kr.co.lunatalk.domain.image.domain.ImageType

interface ImageRepositoryCustom {
    fun fetchProductImagesByProductId(productId: Long): List<Image>
    fun fetchProductImagesByProductIds(productIds: List<Long>): List<Image>
    fun findAllByReferenceIdAndImageType(referenceId: Long, imageType: ImageType): List<Image>
}
