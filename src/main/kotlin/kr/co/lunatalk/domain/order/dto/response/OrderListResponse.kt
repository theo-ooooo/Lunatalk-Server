package kr.co.lunatalk.domain.order.dto.response

import kr.co.lunatalk.domain.order.domain.Order
import java.time.LocalDateTime

data class OrderListResponse(
    val orderId: Long?,
    val orderNumber: String?,
    val status: String?,
    val totalPrice: Long?,
    val createdAt: LocalDateTime?,
    val nickname: String?,
    val orderItems: List<OrderItemResponse>
) {
    companion object {
        fun from(order: Order): OrderListResponse {
            return OrderListResponse(
                orderId = order.id,
                orderNumber = order.orderNumber,
                status = order.status?.value,
                totalPrice = order.totalPrice,
                createdAt = order.createdAt,
                nickname = order.member?.profile?.nickname,
                orderItems = order.orderItems.map { OrderItemResponse.from(it) }
            )
        }
    }
}
