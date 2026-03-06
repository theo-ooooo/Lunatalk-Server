package kr.co.lunatalk.domain.payment.dto.request

import io.swagger.v3.oas.annotations.media.Schema

data class PaymentCancelRequest(
    @Schema(description = "주문 번호 (Toss orderId, 서버의 orderNumber와 동일)", example = "L3ABCDEFG")
    val orderId: String,

    @Schema(description = "결제 취소 사유", example = "고객 단순 변심")
    val cancelReason: String
)
