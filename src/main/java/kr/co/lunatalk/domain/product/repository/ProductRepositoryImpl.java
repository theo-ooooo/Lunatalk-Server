package kr.co.lunatalk.domain.product.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.lunatalk.domain.product.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

	@Override
	public Page<Product> findAll(String productName, Pageable pageable) {
		List<Product> content = queryFactory
			.selectFrom(product)
			.leftJoin(product.productColor, productColor)
			.where(
				productNameEq(productName),
				isActive())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(product.id.desc())
			.fetch();

		Long count = Optional.ofNullable(
			queryFactory
				.select(product.count())
				.from(product)
				.leftJoin(product.productColor, productColor)
				.where(productNameEq(productName),
					isActive()
				)
				.fetchOne()
		).orElse(0L);

		return new PageImpl<>(content, pageable, count);
	}

	private BooleanExpression productNameEq(String productName) {
		return productName != null ? product.name.eq(productName) : null;
	}


	private BooleanExpression isActiveAndVisible() {
		return product.status.eq(ProductStatus.ACTIVE)
			.and(product.visibility.eq(ProductVisibility.VISIBLE));
	}

	private BooleanExpression isActive() {
		return product.status.eq(ProductStatus.ACTIVE);
	}
}
