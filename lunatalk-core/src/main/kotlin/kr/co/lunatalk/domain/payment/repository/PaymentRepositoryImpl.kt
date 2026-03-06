package kr.co.lunatalk.domain.payment.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.co.lunatalk.domain.payment.domain.PaymentStatus
import kr.co.lunatalk.domain.payment.domain.QPayment.payment
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class PaymentRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : PaymentRepositoryCustom {

    override fun sumAmountByStatusAndApprovedAtBetween(
        status: PaymentStatus,
        start: LocalDateTime,
        end: LocalDateTime,
    ): Long {
        val result = queryFactory
            .select(payment.amount.sum())
            .from(payment)
            .where(
                payment.status.eq(status),
                payment.approvedAt.isNotNull,
                payment.approvedAt.between(start, end)
            )
            .fetchOne()

        return result ?: 0L
    }

    override fun countByStatusAndApprovedAtBetween(
        status: PaymentStatus,
        start: LocalDateTime,
        end: LocalDateTime,
    ): Long {
        val result = queryFactory
            .select(payment.count())
            .from(payment)
            .where(
                payment.status.eq(status),
                payment.approvedAt.isNotNull,
                payment.approvedAt.between(start, end)
            )
            .fetchOne()

        return result ?: 0L
    }
}
