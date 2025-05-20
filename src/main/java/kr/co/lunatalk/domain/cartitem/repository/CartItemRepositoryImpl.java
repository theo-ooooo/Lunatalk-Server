package kr.co.lunatalk.domain.cartitem.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.lunatalk.domain.cartitem.domain.CartItem;
import kr.co.lunatalk.domain.member.domain.QMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static kr.co.lunatalk.domain.cartitem.domain.QCartItem.cartItem;
import static kr.co.lunatalk.domain.member.domain.QMember.member;

@Repository
@RequiredArgsConstructor
public class CartItemRepositoryImpl implements CartItemRepositoryCustom {
	private final JPAQueryFactory queryFactory;
	@Override
	public List<CartItem> findByMemberId(Long memberId) {
		return queryFactory
			.selectFrom(cartItem)
			.innerJoin(cartItem.member, member).fetchJoin()
			.where(cartItem.member.id.eq(memberId))
			.fetch();
	}
}
