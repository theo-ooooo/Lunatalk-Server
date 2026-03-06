package kr.co.lunatalk.domain.payment.repository

import kr.co.lunatalk.domain.payment.domain.PaymentStatus
import java.time.LocalDateTime

interface PaymentRepositoryCustom {
    fun sumAmountByStatusAndApprovedAtBetween(
        status: PaymentStatus,
        start: LocalDateTime,
        end: LocalDateTime,
    ): Long

    fun countByStatusAndApprovedAtBetween(
        status: PaymentStatus,
        start: LocalDateTime,
        end: LocalDateTime,
    ): Long
}
