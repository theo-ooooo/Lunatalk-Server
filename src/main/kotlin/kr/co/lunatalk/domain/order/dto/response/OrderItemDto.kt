package kr.co.lunatalk.domain.order.dto.response

import kr.co.lunatalk.domain.order.domain.OrderItem

data class OrderItemDto(
    val productId: Long?,
    val productName: String?,
    val price: Long?,
    val quantity: Int?,
    val totalPrice: Long?,
    val color: String?
) {
    companion object {
        fun from(orderItem: OrderItem): OrderItemDto {
            return OrderItemDto(
                productId = orderItem.productId,
                productName = orderItem.productName,
                price = orderItem.price,
                quantity = orderItem.quantity,
                totalPrice = orderItem.totalPrice,
                color = orderItem.optionSnapshot?.color
            )
        }
    }
}
