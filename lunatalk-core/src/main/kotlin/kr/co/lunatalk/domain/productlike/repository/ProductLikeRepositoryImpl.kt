package kr.co.lunatalk.domain.productlike.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.co.lunatalk.domain.productlike.domain.QProductLike.productLike
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
class ProductLikeRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : ProductLikeRepositoryCustom {

    override fun countByProductId(productId: Long): Long? {
        return queryFactory
            .select(productLike.count())
            .from(productLike)
            .where(productIdEq(productId))
            .fetchOne()
    }

    override fun countByProductIds(productIds: List<Long>): Map<Long, Long> {
        if (productIds.isEmpty()) {
            return HashMap()
        }

        val results = queryFactory
            .select(productLike.product.id, productLike.count())
            .from(productLike)
            .where(productLike.product.id.`in`(productIds))
            .groupBy(productLike.product.id)
            .fetch()
            .map { tuple ->
                CountResult(
                    tuple.get(productLike.product.id)!!,
                    tuple.get(productLike.count())!!
                )
            }

        val countMap = HashMap<Long, Long>()
        for (productId in productIds) {
            countMap[productId] = 0L
        }
        for (result in results) {
            countMap[result.productId] = result.count
        }
        return countMap
    }

    override fun existsByMemberIdAndProductIds(memberId: Long, productIds: List<Long>): Map<Long, Boolean> {
        val likedProductIds = queryFactory
            .select(productLike.product.id)
            .from(productLike)
            .where(
                productLike.member.id.eq(memberId),
                productLike.product.id.`in`(productIds)
            )
            .fetch()

        val resultMap = HashMap<Long, Boolean>()
        for (productId in productIds) {
            resultMap[productId] = likedProductIds.contains(productId)
        }
        return resultMap
    }

    override fun findLikedProductIdsByMemberId(memberId: Long, pageable: Pageable): Page<Long> {
        val likedProductIds = queryFactory
            .select(productLike.product.id)
            .from(productLike)
            .where(productLike.member.id.eq(memberId))
            .orderBy(productLike.createdAt.desc(), productLike.id.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val total = Optional.ofNullable(
            queryFactory
                .select(productLike.count())
                .from(productLike)
                .where(productLike.member.id.eq(memberId))
                .fetchOne()
        ).orElse(0L)

        return PageImpl(likedProductIds, pageable, total)
    }

    private fun productIdEq(productId: Long?): BooleanExpression? {
        return productId?.let { productLike.product.id.eq(it) }
    }

    private data class CountResult(val productId: Long, val count: Long)
}
