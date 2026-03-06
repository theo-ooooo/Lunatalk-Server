package kr.co.lunatalk.domain.order.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import kr.co.lunatalk.domain.delivery.dto.response.DeliveryFindResponse
import kr.co.lunatalk.domain.member.dto.response.MemberInfoResponse
import kr.co.lunatalk.domain.order.domain.Order
import java.time.LocalDateTime

data class OrderFindResponse(
    @Schema(description = "주문 ID", defaultValue = "1")
    val orderId: Long?,

    @Schema(description = "주문 번호", defaultValue = "L3ABCDEFG")
    val orderNumber: String?,

    @Schema(description = "주문 상태", defaultValue = "ORDERED")
    val status: String?,

    @Schema(description = "주문 총 금액", defaultValue = "1000")
    val totalPrice: Long?,

    @Schema(description = "구매 상품들")
    val orderItems: List<OrderItemResponse>,

    @Schema(description = "배송 정보")
    val deliveries: List<DeliveryFindResponse>,

    @Schema(description = "회원 정보")
    val member: MemberInfoResponse?,

    @Schema(description = "주문일")
    val createdAt: LocalDateTime?
) {
    companion object {
        fun from(order: Order): OrderFindResponse {
            return OrderFindResponse(
                orderId = order.id,
                orderNumber = order.orderNumber,
                status = order.status?.value,
                totalPrice = order.totalPrice,
                orderItems = order.orderItems.map { OrderItemResponse.from(it) },
                deliveries = order.deliverys.map { DeliveryFindResponse.from(it) },
                member = order.member?.let { MemberInfoResponse.from(it) },
                createdAt = order.createdAt
            )
        }
    }
}
