package kr.co.lunatalk.domain.payment.dto.toss

data class TossPaymentConfirmRequest(
    val paymentKey: String,
    val orderId: String,
    val amount: Long
)
