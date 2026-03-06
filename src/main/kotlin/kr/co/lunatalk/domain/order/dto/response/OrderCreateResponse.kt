package kr.co.lunatalk.domain.order.dto.response

import io.swagger.v3.oas.annotations.media.Schema

data class OrderCreateResponse(
    @Schema(name = "회원용_주문번호")
    val orderNumber: String,

    @Schema(name = "관리자용_주문번호")
    val orderId: Long
) {
    companion object {
        fun of(orderNumber: String, orderId: Long): OrderCreateResponse {
            return OrderCreateResponse(orderNumber, orderId)
        }
    }
}
