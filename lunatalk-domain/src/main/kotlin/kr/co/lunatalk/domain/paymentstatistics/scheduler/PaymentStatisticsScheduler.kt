package kr.co.lunatalk.domain.paymentstatistics.scheduler

import kr.co.lunatalk.domain.paymentstatistics.service.PaymentStatisticsService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class PaymentStatisticsScheduler(
    private val paymentStatisticsService: PaymentStatisticsService,
) {

    private val log = LoggerFactory.getLogger(PaymentStatisticsScheduler::class.java)

    @Scheduled(cron = "0 0 0 * * *")
    fun aggregateYesterdayStatistics() {
        log.info("전날 결제 통계 집계 시작")
        val yesterday = LocalDate.now().minusDays(1)
        paymentStatisticsService.aggregateStatistics(yesterday)
        log.info("전날 결제 통계 집계 완료: {}", yesterday)
    }

    @Scheduled(cron = "0 0 * * * *")
    fun updateTodayStatistics() {
        log.info("오늘 결제 통계 업데이트 시작")
        paymentStatisticsService.aggregateTodayStatistics()
        log.info("오늘 결제 통계 업데이트 완료")
    }
}
