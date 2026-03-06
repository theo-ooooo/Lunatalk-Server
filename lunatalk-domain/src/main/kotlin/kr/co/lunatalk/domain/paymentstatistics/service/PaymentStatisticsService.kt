package kr.co.lunatalk.domain.paymentstatistics.service

import kr.co.lunatalk.domain.payment.domain.PaymentStatus
import kr.co.lunatalk.domain.payment.repository.PaymentRepository
import kr.co.lunatalk.domain.paymentstatistics.domain.PaymentStatistics
import kr.co.lunatalk.domain.paymentstatistics.repository.PaymentStatisticsRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime

@Service
@Transactional
class PaymentStatisticsService(
    private val paymentRepository: PaymentRepository,
    private val paymentStatisticsRepository: PaymentStatisticsRepository,
) {

    fun aggregateTodayStatistics() {
        val today = LocalDate.now()
        aggregateStatistics(today)
    }

    fun aggregateStatistics(date: LocalDate) {
        val startOfDay = date.atStartOfDay()
        val endOfDay = date.atTime(LocalTime.MAX)

        val totalSales = paymentRepository.sumAmountByStatusAndApprovedAtBetween(
            PaymentStatus.SUCCESS, startOfDay, endOfDay
        )

        val orderCount = paymentRepository.countByStatusAndApprovedAtBetween(
            PaymentStatus.SUCCESS, startOfDay, endOfDay
        )

        val statistics = paymentStatisticsRepository.findByStatisticsDate(date)
            .orElse(PaymentStatistics.create(date, totalSales, orderCount))

        statistics.updateStatistics(totalSales, orderCount)
        paymentStatisticsRepository.save(statistics)
    }

    @Transactional(readOnly = true)
    fun getTodayStatistics(): PaymentStatistics {
        val today = LocalDate.now()
        return paymentStatisticsRepository.findByStatisticsDate(today)
            .orElse(PaymentStatistics.create(today, 0L, 0L))
    }
}
