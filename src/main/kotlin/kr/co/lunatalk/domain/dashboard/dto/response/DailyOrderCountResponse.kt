package kr.co.lunatalk.domain.dashboard.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

data class DailyOrderCountResponse(
    @Schema(description = "날짜")
    val date: LocalDate,

    @Schema(description = "주문 수")
    val orderCount: Long,
)
