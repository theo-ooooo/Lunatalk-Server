package kr.co.lunatalk.domain.cartitem.repository

import kr.co.lunatalk.domain.cartitem.domain.CartItem

interface CartItemRepositoryCustom {
    fun findByMemberId(memberId: Long): List<CartItem>
}
