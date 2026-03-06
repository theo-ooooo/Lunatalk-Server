package kr.co.lunatalk.domain.order.dto.response

import kr.co.lunatalk.domain.order.domain.OrderItem

data class OrderItemResponse(
    val productId: Long?,
    val productName: String?,
    val price: Long?,
    val quantity: Int?,
    val totalPrice: Long?,
    val color: String?,
    val productImageUrl: String?
) {
    companion object {
        fun from(item: OrderItem): OrderItemResponse {
            return OrderItemResponse(
                productId = item.productId,
                productName = item.productName,
                price = item.price,
                quantity = item.quantity,
                totalPrice = item.totalPrice,
                color = item.optionSnapshot?.color,
                productImageUrl = null
            )
        }

        fun from(item: OrderItem, productImageUrl: String?): OrderItemResponse {
            return OrderItemResponse(
                productId = item.productId,
                productName = item.productName,
                price = item.price,
                quantity = item.quantity,
                totalPrice = item.totalPrice,
                color = item.optionSnapshot?.color,
                productImageUrl = productImageUrl
            )
        }
    }
}
