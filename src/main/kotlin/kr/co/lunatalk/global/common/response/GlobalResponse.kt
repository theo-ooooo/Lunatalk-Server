package kr.co.lunatalk.global.common.response

import java.time.LocalDateTime

data class GlobalResponse(
    val isSuccess: Boolean,
    val status: Int,
    val data: Any?,
    val timestamp: LocalDateTime
) {
    companion object {
        fun success(status: Int, data: Any?): GlobalResponse =
            GlobalResponse(true, status, data, LocalDateTime.now())

        fun fail(status: Int, errorResponse: ErrorResponse): GlobalResponse =
            GlobalResponse(false, status, errorResponse, LocalDateTime.now())
    }
}
