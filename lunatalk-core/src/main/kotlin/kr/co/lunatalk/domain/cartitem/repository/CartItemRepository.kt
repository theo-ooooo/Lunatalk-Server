package kr.co.lunatalk.domain.cartitem.repository

import kr.co.lunatalk.domain.cartitem.domain.CartItem
import org.springframework.data.jpa.repository.JpaRepository

interface CartItemRepository : JpaRepository<CartItem, Long>, CartItemRepositoryCustom {

    fun deleteByMemberIdAndProductId(memberId: Long, productId: Long)
}
