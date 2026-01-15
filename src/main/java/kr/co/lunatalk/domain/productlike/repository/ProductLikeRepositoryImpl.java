package kr.co.lunatalk.domain.productlike.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static kr.co.lunatalk.domain.productlike.domain.QProductLike.productLike;

@Repository
@RequiredArgsConstructor
public class ProductLikeRepositoryImpl implements ProductLikeRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Long countByProductId(Long productId) {
		return queryFactory
			.select(productLike.count())
			.from(productLike)
			.where(productIdEq(productId))
			.fetchOne();
	}

	@Override
	public Map<Long, Long> countByProductIds(List<Long> productIds) {
		if (productIds.isEmpty()) {
			return new HashMap<>();
		}

		List<CountResult> results = queryFactory
			.select(productLike.product.id, productLike.count())
			.from(productLike)
			.where(productLike.product.id.in(productIds))
			.groupBy(productLike.product.id)
			.fetch()
			.stream()
			.map(tuple -> new CountResult(
				tuple.get(productLike.product.id),
				tuple.get(productLike.count())
			))
			.toList();

		Map<Long, Long> countMap = new HashMap<>();
		for (Long productId : productIds) {
			countMap.put(productId, 0L);
		}
		for (CountResult result : results) {
			countMap.put(result.productId, result.count);
		}
		return countMap;
	}

	@Override
	public Map<Long, Boolean> existsByMemberIdAndProductIds(Long memberId, List<Long> productIds) {
		List<Long> likedProductIds = queryFactory
			.select(productLike.product.id)
			.from(productLike)
			.where(
				productLike.member.id.eq(memberId),
				productLike.product.id.in(productIds)
			)
			.fetch();

		Map<Long, Boolean> resultMap = new HashMap<>();
		for (Long productId : productIds) {
			resultMap.put(productId, likedProductIds.contains(productId));
		}
		return resultMap;
	}

	@Override
	public Page<Long> findLikedProductIdsByMemberId(Long memberId, Pageable pageable) {
		List<Long> likedProductIds = queryFactory
			.select(productLike.product.id)
			.from(productLike)
			.where(productLike.member.id.eq(memberId))
			.orderBy(productLike.createdAt.desc(), productLike.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = Optional.ofNullable(
			queryFactory
				.select(productLike.count())
				.from(productLike)
				.where(productLike.member.id.eq(memberId))
				.fetchOne()
		).orElse(0L);

		return new PageImpl<>(likedProductIds, pageable, total);
	}

	private BooleanExpression productIdEq(Long productId) {
		return productId != null ? productLike.product.id.eq(productId) : null;
	}

	private record CountResult(Long productId, Long count) {
	}
}

