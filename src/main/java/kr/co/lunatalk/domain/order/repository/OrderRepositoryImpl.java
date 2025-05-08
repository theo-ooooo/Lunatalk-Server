package kr.co.lunatalk.domain.order.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.lunatalk.domain.order.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static kr.co.lunatalk.domain.member.domain.QMember.*;
import static kr.co.lunatalk.domain.order.domain.QOrder.order;
import static kr.co.lunatalk.domain.order.domain.QOrderItem.*;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public Order findByOrderWithItems(String orderNumber) {
		return queryFactory
			.selectFrom(order)
			.innerJoin(order.orderItems, orderItem).fetchJoin()
			.innerJoin(order.member, member).fetchJoin()
			.where(orderNumberEq(orderNumber))
			.fetchOne();
	}

	private static BooleanExpression orderNumberEq(String orderNumber) {
		return order.orderNumber.eq(orderNumber);
	}
}
