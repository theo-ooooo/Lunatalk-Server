package kr.co.lunatalk.domain.product.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.co.lunatalk.domain.product.domain.Product
import kr.co.lunatalk.domain.product.domain.ProductStatus
import kr.co.lunatalk.domain.product.domain.ProductVisibility
import kr.co.lunatalk.domain.product.domain.QProduct.product
import kr.co.lunatalk.domain.product.domain.QProductColor.productColor
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
class ProductRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : ProductRepositoryCustom {

    override fun findProductById(productId: Long): Optional<Product> {
        return Optional.ofNullable(
            queryFactory
                .selectFrom(product)
                .leftJoin(product.productColor).fetchJoin()
                .where(
                    product.id.eq(productId)
                        .and(isActiveAndVisible())
                )
                .fetchOne()
        )
    }

    override fun findAllProductsByProductIds(productIds: List<Long>): List<Product> {
        return queryFactory
            .selectFrom(product)
            .where(
                product.id.`in`(productIds)
                    .and(isActiveAndVisible())
            )
            .fetch()
    }

    override fun findAllProductDtoByIdsWithJoin(productIds: List<Long>): List<Product> {
        return queryFactory
            .selectFrom(product)
            .leftJoin(product.productColor, productColor)
            .fetchJoin()
            .where(
                product.id.`in`(productIds)
                    .and(isActiveAndVisible())
            )
            .fetch()
    }

    override fun findAll(productName: String?, pageable: Pageable): Page<Product> {
        val content = queryFactory
            .selectFrom(product)
            .leftJoin(product.productColor, productColor)
            .where(
                productNameEq(productName),
                isActive()
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(product.id.desc())
            .fetch()

        val count = Optional.ofNullable(
            queryFactory
                .select(product.count())
                .from(product)
                .leftJoin(product.productColor, productColor)
                .where(
                    productNameEq(productName),
                    isActive()
                )
                .fetchOne()
        ).orElse(0L)

        return PageImpl(content, pageable, count)
    }

    private fun productNameEq(productName: String?): BooleanExpression? {
        if (productName == null) return null
        val keyword = productName.trim()
        if (keyword.isEmpty()) return null
        return product.name.containsIgnoreCase(keyword)
    }

    private fun isActiveAndVisible(): BooleanExpression {
        return product.status.eq(ProductStatus.ACTIVE)
            .and(product.visibility.eq(ProductVisibility.VISIBLE))
    }

    private fun isActive(): BooleanExpression {
        return product.status.eq(ProductStatus.ACTIVE)
    }
}
