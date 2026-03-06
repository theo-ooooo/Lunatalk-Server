package kr.co.lunatalk.domain.order.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.co.lunatalk.domain.delivery.domain.QDelivery.delivery
import kr.co.lunatalk.domain.member.domain.QMember.member
import kr.co.lunatalk.domain.order.domain.Order
import kr.co.lunatalk.domain.order.domain.OrderStatus
import kr.co.lunatalk.domain.order.domain.QOrder.order
import kr.co.lunatalk.domain.order.domain.QOrderItem.orderItem
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
class OrderRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : OrderRepositoryCustom {

    override fun findByOrderWithItems(orderNumber: String): Optional<Order> {
        return Optional.ofNullable(
            queryFactory
                .selectFrom(order)
                .join(order.orderItems, orderItem).fetchJoin()
                .join(order.member, member).fetchJoin()
                .where(orderNumberEq(orderNumber))
                .fetchOne()
        )
    }

    override fun findOrdersWithItemsByMemberId(memberId: Long, pageable: Pageable): Page<Order> {
        val content = queryFactory.selectFrom(order)
            .innerJoin(order.orderItems, orderItem)
            .where(memberIdEq(memberId), orderStatusNotPending())
            .orderBy(order.createdAt.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val total = queryFactory
            .select(order.count())
            .from(order)
            .where(memberIdEq(memberId), orderStatusNotPending())
            .fetchOne() ?: 0L

        return PageImpl(content, pageable, total)
    }

    override fun findOrders(
        orderNumber: String?,
        status: OrderStatus?,
        username: String?,
        email: String?,
        nickname: String?,
        phone: String?,
        pageable: Pageable
    ): Page<Order> {
        val content = queryFactory
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
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(order.createdAt.desc())
            .fetch()

        val total = queryFactory
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
            )
            .fetchOne() ?: 0L

        return PageImpl(content, pageable, total)
    }

    private fun containsOrderNumber(orderNumber: String?): BooleanExpression? {
        return orderNumber?.let { order.orderNumber.contains(it) }
    }

    private fun containsUsername(username: String?): BooleanExpression? {
        return username?.let { order.member.username.contains(it) }
    }

    private fun statusEq(status: OrderStatus?): BooleanExpression? {
        return status?.let { order.status.eq(it) }
    }

    private fun containsEmail(email: String?): BooleanExpression? {
        return email?.let { order.member.email.contains(it) }
    }

    private fun containsNickname(nickname: String?): BooleanExpression? {
        return nickname?.let { order.member.profile.nickname.contains(it) }
    }

    private fun containsPhone(phone: String?): BooleanExpression? {
        return phone?.let { order.member.phone.contains(it) }
    }

    companion object {
        private fun memberIdEq(memberId: Long?): BooleanExpression? {
            return memberId?.let { order.member.id.eq(it) }
        }

        private fun orderNumberEq(orderNumber: String?): BooleanExpression? {
            return orderNumber?.let { order.orderNumber.eq(it) }
        }

        private fun orderStatusNotPending(): BooleanExpression {
            return order.status.notIn(OrderStatus.ORDER_PENDING)
        }
    }
}
