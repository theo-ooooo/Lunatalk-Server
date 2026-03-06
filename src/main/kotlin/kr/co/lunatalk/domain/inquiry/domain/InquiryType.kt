package kr.co.lunatalk.domain.inquiry.domain

enum class InquiryType(val description: String) {
    PRODUCT("상품"),
    ORDER("주문"),
    GENERAL("일반")
}
