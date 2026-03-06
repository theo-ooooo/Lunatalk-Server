package kr.co.lunatalk.domain.payment.domain

enum class PaymentStatus(val description: String) {
    READY("결제 대기"),
    SUCCESS("결제 성공"),
    FAILED("결제 실패"),
    CANCELLED("결제 취소")
}
