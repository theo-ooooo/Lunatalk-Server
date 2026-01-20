package kr.co.lunatalk.domain.dashboard.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record DailyOrderCountResponse(
	@Schema(description = "날짜")
	LocalDate date,

	@Schema(description = "주문 수")
	Long orderCount
) {
}

