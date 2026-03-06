package kr.co.lunatalk.domain.image.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.co.lunatalk.domain.image.domain.Image
import kr.co.lunatalk.domain.image.domain.ImageStatus
import kr.co.lunatalk.domain.image.domain.ImageType
import kr.co.lunatalk.domain.image.domain.QImage.image
import org.springframework.stereotype.Repository

@Repository
class ImageRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : ImageRepositoryCustom {

    override fun fetchProductImagesByProductId(productId: Long): List<Image> {
        return queryFactory
            .selectFrom(image)
            .where(
                image.referenceId.eq(productId)
                    .and(image.imageType.`in`(ImageType.PRODUCT_CONTENT, ImageType.PRODUCT_THUMBNAIL)),
                isImageCompleted()
            )
            .fetch()
    }

    override fun fetchProductImagesByProductIds(productIds: List<Long>): List<Image> {
        return queryFactory
            .selectFrom(image)
            .where(
                image.referenceId.`in`(productIds)
                    .and(image.imageType.`in`(ImageType.PRODUCT_CONTENT, ImageType.PRODUCT_THUMBNAIL)),
                isImageCompleted()
            )
            .fetch()
    }

    override fun findAllByReferenceIdAndImageType(referenceId: Long, imageType: ImageType): List<Image> {
        return queryFactory
            .selectFrom(image)
            .where(
                image.referenceId.eq(referenceId)
                    .and(image.imageType.eq(imageType))
                    .and(isImageCompleted())
            )
            .fetch()
    }

    private fun isImageCompleted(): BooleanExpression {
        return image.imageStatus.eq(ImageStatus.COMPLETED)
    }
}
