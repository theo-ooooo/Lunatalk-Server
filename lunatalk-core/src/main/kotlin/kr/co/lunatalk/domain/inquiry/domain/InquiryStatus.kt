package kr.co.lunatalk.domain.inquiry.domain

enum class InquiryStatus(val description: String) {
    PENDING("대기중"),
    ANSWERED("답변완료"),
    CLOSED("종료")
}
