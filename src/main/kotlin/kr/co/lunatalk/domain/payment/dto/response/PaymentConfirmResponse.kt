package kr.co.lunatalk.domain.payment.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import kr.co.lunatalk.domain.order.domain.Order
import kr.co.lunatalk.domain.order.domain.OrderStatus
import kr.co.lunatalk.domain.payment.domain.Payment
import kr.co.lunatalk.domain.payment.domain.PaymentStatus
import java.time.LocalDateTime

data class PaymentConfirmResponse(
    @Schema(description = "주문 번호", example = "L3ABCDEFG")
    val orderNumber: String?,

    @Schema(description = "결제 금액", example = "10000")
    val amount: Long?,

    @Schema(description = "결제 상태", example = "SUCCESS")
    val paymentStatus: PaymentStatus?,

    @Schema(description = "주문 상태", example = "PAYMENT_COMPLETED")
    val orderStatus: OrderStatus?,

    @Schema(description = "토스페이먼츠 paymentKey", example = "pay_2025...")
    val paymentKey: String?,

    @Schema(description = "결제 승인 일시")
    val approvedAt: LocalDateTime?
) {
    companion object {
        fun of(order: Order, payment: Payment): PaymentConfirmResponse {
            return PaymentConfirmResponse(
                orderNumber = order.orderNumber,
                amount = payment.amount,
                paymentStatus = payment.status,
                orderStatus = order.status,
                paymentKey = payment.paymentKey,
                approvedAt = payment.approvedAt
            )
        }
    }
}
