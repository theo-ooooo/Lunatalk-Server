package kr.co.lunatalk.domain.category.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.lunatalk.domain.category.domain.Category;
import kr.co.lunatalk.domain.category.domain.CategoryStatus;
import kr.co.lunatalk.domain.category.domain.CategoryVisibility;
import kr.co.lunatalk.domain.category.domain.QCategory;
import kr.co.lunatalk.domain.product.domain.QProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static kr.co.lunatalk.domain.category.domain.QCategory.category;
import static kr.co.lunatalk.domain.product.domain.QProduct.product;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<Category> findWithProducts(Long categoryId) {
		return Optional.ofNullable(
			queryFactory
				.selectFrom(category)
				.leftJoin(category.products, product).fetchJoin()
				.where(category.id.eq(categoryId).and(isActiveAndVisible()))
				.fetchOne()
		);
	}

	@Override
	public List<Category> findAllWithProducts() {
		return queryFactory
			.selectFrom(category)
			.leftJoin(category.products, product).fetchJoin()
			.fetch();
	}

	public BooleanExpression isActiveAndVisible() {
		return category.visibility.eq(CategoryVisibility.VISIBLE).and(category.status.eq(CategoryStatus.ACTIVE));
	}
}
