package kr.co.lunatalk.global.exception

class CustomException(
    val errorCode: ErrorCode
) : RuntimeException(errorCode.message)
