package kr.co.lunatalk.domain.dashboard.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record DashboardResponse(
	@Schema(description = "등록된 상품 수")
	Long productCount,

	@Schema(description = "등록된 회원 수")
	Long memberCount,

	@Schema(description = "오늘 주문 수")
	Long todayOrderCount,

	@Schema(description = "진행중인 기획전 수")
	Long activeExhibitionCount,

	@Schema(description = "등록된 카테고리 수")
	Long categoryCount,

	@Schema(description = "오늘 매출")
	Long todaySales,

	@Schema(description = "최근 7일 일별 주문 수")
	List<DailyOrderCountResponse> dailyOrderCounts
) {
}

