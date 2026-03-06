package kr.co.lunatalk.domain.paymentstatistics.domain

import jakarta.persistence.*
import kr.co.lunatalk.domain.common.domain.BaseTimeEntity
import java.time.LocalDate

@Entity
@Table(
    name = "payment_statistics", uniqueConstraints = [
        UniqueConstraint(columnNames = ["statistics_date"])
    ]
)
open class PaymentStatistics protected constructor() : BaseTimeEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null
        protected set

    @Column(nullable = false, unique = true)
    open var statisticsDate: LocalDate = LocalDate.now()
        protected set

    @Column(nullable = false)
    open var totalSales: Long = 0L
        protected set

    @Column(nullable = false)
    open var orderCount: Long = 0L
        protected set

    private constructor(
        statisticsDate: LocalDate,
        totalSales: Long,
        orderCount: Long,
    ) : this() {
        this.statisticsDate = statisticsDate
        this.totalSales = totalSales
        this.orderCount = orderCount
    }

    fun updateStatistics(totalSales: Long?, orderCount: Long?) {
        this.totalSales = totalSales ?: 0L
        this.orderCount = orderCount ?: 0L
    }

    companion object {
        fun create(statisticsDate: LocalDate, totalSales: Long?, orderCount: Long?): PaymentStatistics {
            return PaymentStatistics(
                statisticsDate = statisticsDate,
                totalSales = totalSales ?: 0L,
                orderCount = orderCount ?: 0L
            )
        }
    }
}
