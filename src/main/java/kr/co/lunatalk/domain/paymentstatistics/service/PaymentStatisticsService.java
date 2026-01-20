package kr.co.lunatalk.domain.paymentstatistics.service;

import kr.co.lunatalk.domain.payment.domain.PaymentStatus;
import kr.co.lunatalk.domain.payment.repository.PaymentRepository;
import kr.co.lunatalk.domain.paymentstatistics.domain.PaymentStatistics;
import kr.co.lunatalk.domain.paymentstatistics.repository.PaymentStatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentStatisticsService {

	private final PaymentRepository paymentRepository;
	private final PaymentStatisticsRepository paymentStatisticsRepository;

	public void aggregateTodayStatistics() {
		LocalDate today = LocalDate.now();
		aggregateStatistics(today);
	}

	public void aggregateStatistics(LocalDate date) {
		LocalDateTime startOfDay = date.atStartOfDay();
		LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

		// 결제 완료된 금액 합계
		Long totalSales = paymentRepository.sumAmountByStatusAndApprovedAtBetween(
			PaymentStatus.SUCCESS, startOfDay, endOfDay);

		// 결제 완료된 주문 수 (Payment 테이블에서 집계)
		Long orderCount = paymentRepository.countByStatusAndApprovedAtBetween(
			PaymentStatus.SUCCESS, startOfDay, endOfDay);

		// 통계 저장 또는 업데이트
		PaymentStatistics statistics = paymentStatisticsRepository.findByStatisticsDate(date)
			.orElse(PaymentStatistics.create(date, totalSales, orderCount));

		statistics.updateStatistics(totalSales, orderCount);
		paymentStatisticsRepository.save(statistics);
	}

	@Transactional(readOnly = true)
	public PaymentStatistics getTodayStatistics() {
		LocalDate today = LocalDate.now();
		return paymentStatisticsRepository.findByStatisticsDate(today)
			.orElse(PaymentStatistics.create(today, 0L, 0L));
	}
}

