package kr.co.lunatalk.domain.image.domain

enum class ImageStatus(val status: String) {
    PENDING("pending"),
    COMPLETED("completed"),
    DELETED("deleted")
}
