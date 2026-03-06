package kr.co.lunatalk.domain.dashboard.dto.response

import io.swagger.v3.oas.annotations.media.Schema

data class DashboardResponse(
    @Schema(description = "등록된 상품 수")
    val productCount: Long,

    @Schema(description = "등록된 회원 수")
    val memberCount: Long,

    @Schema(description = "오늘 주문 수")
    val todayOrderCount: Long,

    @Schema(description = "진행중인 기획전 수")
    val activeExhibitionCount: Long,

    @Schema(description = "등록된 카테고리 수")
    val categoryCount: Long,

    @Schema(description = "오늘 매출")
    val todaySales: Long,

    @Schema(description = "최근 7일 일별 주문 수")
    val dailyOrderCounts: List<DailyOrderCountResponse>,
)
