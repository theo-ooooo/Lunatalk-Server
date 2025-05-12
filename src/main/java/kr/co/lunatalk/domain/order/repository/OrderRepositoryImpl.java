package kr.co.lunatalk.domain.order.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.lunatalk.domain.order.domain.Order;
import kr.co.lunatalk.domain.order.domain.OrderStatus;
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

	@Override
	public Page<Order> findOrders(String orderNumber, OrderStatus status, String username, String email, String nickname, String phone, Pageable pageable) {
		List<Order> content = queryFactory
			.selectFrom(order)
			.innerJoin(order.member, member).fetchJoin()
			.where(
				containsOrderNumber(orderNumber),
				statusEq(status),
				containsUsername(username),
				containsEmail(email),
				containsNickname(nickname),
				containsPhone(phone)
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(order.createdAt.desc())
			.fetch();

		Long total = Optional.ofNullable(
			queryFactory
				.select(order.count())
				.from(order)
				.innerJoin(order.member, member)
				.where(
					containsOrderNumber(orderNumber),
					statusEq(status),
					containsUsername(username),
					containsEmail(email),
					containsNickname(nickname),
					containsPhone(phone)
				).fetchOne()
		).orElse(0L);

		return new PageImpl<>(content, pageable, total);
	}

	private BooleanExpression containsOrderNumber(String orderNumber) {
		return orderNumber != null ? order.orderNumber.contains(orderNumber) : null;
	}

	private BooleanExpression containsUsername(String username) {
		return username != null ? order.member.username.contains(username) : null;
	}

	private BooleanExpression statusEq(OrderStatus status) {
		return status != null ? order.status.eq(status) : null;
	}

	private BooleanExpression containsEmail(String email) {
		return email != null ? order.member.email.contains(email) : null;
	}

	private BooleanExpression containsNickname(String nickname) {
		return nickname != null ? order.member.profile.nickname.contains(nickname) : null;
	}

	private BooleanExpression containsPhone(String phone) {
		return phone != null ? order.member.phone.contains(phone) : null;
	}

	private static BooleanExpression memberIdEq(Long memberId) {
		return memberId != null ? order.member.id.eq(memberId) : null;
	}

	private static BooleanExpression orderNumberEq(String orderNumber) {

		return orderNumber != null ? order.orderNumber.eq(orderNumber) : null;
	}
}
