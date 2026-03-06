package kr.co.lunatalk.domain.delivery.domain

enum class DeliveryStatus(val value: String) {
    READY("배송 준비중"),
    SHIPPED("배송중"),
    DELIVERED("배송 완료"),
    RETURNED("반품"),
    REDELIVERY("재배송")
}
