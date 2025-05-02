package kr.co.lunatalk.domain.product.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.lunatalk.domain.product.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static kr.co.lunatalk.domain.image.domain.QImage.image;
import static kr.co.lunatalk.domain.product.domain.QProduct.product;
import static kr.co.lunatalk.domain.product.domain.QProductColor.productColor;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Product findProductById(Long productId) {
		return queryFactory
			.selectFrom(product)
			.leftJoin(product.productColor).fetchJoin()
			.where(product.id.eq(productId)
				.and(isActiveAndVisible()))
			.fetchOne();
	}

	@Override
	public List<Product> findAllProductsByProductIds(List<Long> productIds) {
		return queryFactory
			.selectFrom(product)
			.where(
				product.id.in(productIds)
					.and(isActiveAndVisible())
			).fetch();
	}

	@Override
	public List<Product> findAllProductDtoByIdsWithJoin(List<Long> productIds) {
		return queryFactory
			.selectFrom(product)
			.leftJoin(product.productColor, productColor)
			.fetchJoin()
			.where(
				product.id.in(productIds)
					.and(isActiveAndVisible())
			).fetch();
	}

	private List<ProductColor> fetchColorsByProductId(Long productId) {
		return queryFactory
			.selectFrom(productColor)
			.where(productColor.product.id.eq(productId))
			.fetch();
	}

	private BooleanExpression isActiveAndVisible() {
		return product.status.eq(ProductStatus.ACTIVE)
			.and(product.visibility.eq(ProductVisibility.VISIBLE));
	}
}
