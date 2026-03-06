package kr.co.lunatalk.domain.payment.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import kr.co.lunatalk.domain.order.domain.Order
import kr.co.lunatalk.domain.order.domain.OrderStatus
import kr.co.lunatalk.domain.payment.domain.Payment
import kr.co.lunatalk.domain.payment.domain.PaymentStatus

data class PaymentCancelResponse(
    @Schema(description = "주문 번호", example = "L3ABCDEFG")
    val orderNumber: String?,

    @Schema(description = "결제 상태", example = "CANCELLED")
    val paymentStatus: PaymentStatus?,

    @Schema(description = "주문 상태", example = "CANCELLED")
    val orderStatus: OrderStatus?
) {
    companion object {
        fun of(order: Order, payment: Payment): PaymentCancelResponse {
            return PaymentCancelResponse(
                orderNumber = order.orderNumber,
                paymentStatus = payment.status,
                orderStatus = order.status
            )
        }
    }
}
