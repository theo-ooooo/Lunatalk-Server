package kr.co.lunatalk.domain.paymentstatistics.scheduler;

import kr.co.lunatalk.domain.paymentstatistics.service.PaymentStatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentStatisticsScheduler {

	private final PaymentStatisticsService paymentStatisticsService;

	/**
	 * 매일 자정에 전날 통계를 집계합니다.
	 * cron: 초 분 시 일 월 요일
	 * 매일 00:00:00에 실행
	 */
	@Scheduled(cron = "0 0 0 * * *")
	public void aggregateYesterdayStatistics() {
		log.info("전날 결제 통계 집계 시작");
		LocalDate yesterday = LocalDate.now().minusDays(1);
		paymentStatisticsService.aggregateStatistics(yesterday);
		log.info("전날 결제 통계 집계 완료: {}", yesterday);
	}

	/**
	 * 매시간 정각에 오늘 통계를 업데이트합니다.
	 * cron: 초 분 시 일 월 요일
	 * 매시간 0분 0초에 실행
	 */
	@Scheduled(cron = "0 0 * * * *")
	public void updateTodayStatistics() {
		log.info("오늘 결제 통계 업데이트 시작");
		paymentStatisticsService.aggregateTodayStatistics();
		log.info("오늘 결제 통계 업데이트 완료");
	}
}

