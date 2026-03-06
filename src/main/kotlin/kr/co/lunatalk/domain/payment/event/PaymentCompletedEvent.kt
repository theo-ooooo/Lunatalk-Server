package kr.co.lunatalk.domain.payment.event

data class PaymentCompletedEvent(
    val orderNumber: String,
    val orderId: Long,
    val totalAmount: Long,
    val memberEmail: String,
    val items: List<PaymentOrderItem>
) {
    data class PaymentOrderItem(
        val productId: Long,
        val productName: String,
        val quantity: Int,
        val price: Long
    )
}
