package kr.co.lunatalk.domain.cartitem.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.co.lunatalk.domain.cartitem.domain.CartItem
import kr.co.lunatalk.domain.cartitem.domain.QCartItem.cartItem
import kr.co.lunatalk.domain.member.domain.QMember.member
import org.springframework.stereotype.Repository

@Repository
class CartItemRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : CartItemRepositoryCustom {

    override fun findByMemberId(memberId: Long): List<CartItem> {
        return queryFactory
            .selectFrom(cartItem)
            .innerJoin(cartItem.member, member).fetchJoin()
            .where(cartItem.member.id.eq(memberId))
            .fetch()
    }
}
