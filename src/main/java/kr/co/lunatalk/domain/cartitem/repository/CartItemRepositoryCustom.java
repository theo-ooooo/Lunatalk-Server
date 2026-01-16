package kr.co.lunatalk.domain.cartitem.repository;

import kr.co.lunatalk.domain.cartitem.domain.CartItem;

import java.util.List;

public interface CartItemRepositoryCustom {
	List<CartItem> findByMemberId(Long memberId);

}
