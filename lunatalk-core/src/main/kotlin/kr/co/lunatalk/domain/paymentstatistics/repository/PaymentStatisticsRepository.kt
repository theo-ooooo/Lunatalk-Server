package kr.co.lunatalk.domain.paymentstatistics.repository

import kr.co.lunatalk.domain.paymentstatistics.domain.PaymentStatistics
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate
import java.util.Optional

interface PaymentStatisticsRepository : JpaRepository<PaymentStatistics, Long> {
    fun findByStatisticsDate(date: LocalDate): Optional<PaymentStatistics>

    fun findByStatisticsDateBetweenOrderByStatisticsDateDesc(start: LocalDate, end: LocalDate): List<PaymentStatistics>
}
