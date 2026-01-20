package kr.co.lunatalk.domain.paymentstatistics.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.lunatalk.domain.paymentstatistics.service.PaymentStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/admin/payment-statistics")
@RequiredArgsConstructor
@Tag(name = "결제 통계", description = "결제 통계 관리 API")
public class PaymentStatisticsController {

	private final PaymentStatisticsService paymentStatisticsService;

	@PostMapping("/aggregate")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "결제 통계 집계", description = "지정한 날짜의 결제 통계를 수동으로 집계합니다.")
	public ResponseEntity<Void> aggregateStatistics(
		@Parameter(description = "집계할 날짜 (yyyy-MM-dd 형식)", example = "2025-01-19")
		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
	) {
		paymentStatisticsService.aggregateStatistics(date);
		return ResponseEntity.ok().build();
	}
}

