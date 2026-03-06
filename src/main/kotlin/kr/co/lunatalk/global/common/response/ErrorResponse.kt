package kr.co.lunatalk.global.common.response

data class ErrorResponse(
    val errorName: String,
    val message: String
) {
    companion object {
        fun of(errorName: String, message: String): ErrorResponse =
            ErrorResponse(errorName, message)
    }
}
