package kr.co.lunatalk.domain.order.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.lunatalk.domain.order.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static kr.co.lunatalk.domain.member.domain.QMember.*;
import static kr.co.lunatalk.domain.order.domain.QOrder.order;
import static kr.co.lunatalk.domain.order.domain.QOrderItem.*;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<Order> findByOrderWithItems(String orderNumber) {
		return Optional.ofNullable(
			queryFactory
				.selectFrom(order)
				.innerJoin(order.orderItems, orderItem).fetchJoin()
				.innerJoin(order.member, member).fetchJoin()
				.where(orderNumberEq(orderNumber))
				.fetchOne()
		);
	}

	@Override
	public Page<Order> findOrdersWithItemsByMemberId(Long memberId, Pageable pageable) {
		List<Order> content = queryFactory.selectFrom(order)
			.innerJoin(order.orderItems, orderItem).fetchJoin()
			.where(memberIdEq(memberId))
			.orderBy(order.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = Optional.ofNullable(
			queryFactory
				.select(order.count())
				.from(order)
				.where(memberIdEq(memberId))
				.fetchOne()
		).orElse(0L);

		return new PageImpl<>(content, pageable, total);
	}

	private static BooleanExpression memberIdEq(Long memberId) {
		return order.member.id.eq(memberId);
	}

	private static BooleanExpression orderNumberEq(String orderNumber) {
		return order.orderNumber.eq(orderNumber);
	}
}
