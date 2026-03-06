package kr.co.lunatalk.domain.order.repository

import kr.co.lunatalk.domain.order.domain.Order
import kr.co.lunatalk.domain.order.domain.OrderStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.Optional

interface OrderRepositoryCustom {

    fun findByOrderWithItems(orderNumber: String): Optional<Order>

    fun findOrdersWithItemsByMemberId(memberId: Long, pageable: Pageable): Page<Order>

    fun findOrders(
        orderNumber: String?,
        status: OrderStatus?,
        username: String?,
        email: String?,
        nickname: String?,
        phone: String?,
        pageable: Pageable
    ): Page<Order>
}
