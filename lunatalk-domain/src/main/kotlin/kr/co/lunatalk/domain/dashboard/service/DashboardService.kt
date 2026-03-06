package kr.co.lunatalk.domain.dashboard.service

import kr.co.lunatalk.domain.category.repository.CategoryRepository
import kr.co.lunatalk.domain.dashboard.dto.response.DailyOrderCountResponse
import kr.co.lunatalk.domain.dashboard.dto.response.DashboardResponse
import kr.co.lunatalk.domain.exhibition.repository.ExhibitionRepository
import kr.co.lunatalk.domain.member.repository.MemberRepository
import kr.co.lunatalk.domain.order.repository.OrderRepository
import kr.co.lunatalk.domain.paymentstatistics.repository.PaymentStatisticsRepository
import kr.co.lunatalk.domain.paymentstatistics.service.PaymentStatisticsService
import kr.co.lunatalk.domain.product.repository.ProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class DashboardService(
    private val productRepository: ProductRepository,
    private val memberRepository: MemberRepository,
    private val orderRepository: OrderRepository,
    private val exhibitionRepository: ExhibitionRepository,
    private val categoryRepository: CategoryRepository,
    private val paymentStatisticsService: PaymentStatisticsService,
    private val paymentStatisticsRepository: PaymentStatisticsRepository,
) {

    fun getDashboardStatistics(): DashboardResponse {
        val productCount = productRepository.count()
        val memberCount = memberRepository.count()

        val todayStatistics = paymentStatisticsService.getTodayStatistics()
        val todayOrderCount = todayStatistics.orderCount
        val todaySales = todayStatistics.totalSales

        val activeExhibitionCount = exhibitionRepository.countByStartAtLessThanEqualAndEndAtGreaterThanEqual(
            LocalDateTime.now(), LocalDateTime.now()
        )

        val categoryCount = categoryRepository.count()

        val dailyOrderCounts = getDailyOrderCountsForLast7Days()

        return DashboardResponse(
            productCount = productCount,
            memberCount = memberCount,
            todayOrderCount = todayOrderCount,
            activeExhibitionCount = activeExhibitionCount,
            categoryCount = categoryCount,
            todaySales = todaySales,
            dailyOrderCounts = dailyOrderCounts
        )
    }

    private fun getDailyOrderCountsForLast7Days(): List<DailyOrderCountResponse> {
        val today = LocalDate.now()
        val sevenDaysAgo = today.minusDays(6)

        val statistics = paymentStatisticsRepository
            .findByStatisticsDateBetweenOrderByStatisticsDateDesc(sevenDaysAgo, today)

        val statisticsMap = statistics.associate { it.statisticsDate to it.orderCount }

        return (0L..6L).map { days ->
            val date = today.minusDays(days)
            DailyOrderCountResponse(
                date = date,
                orderCount = statisticsMap.getOrDefault(date, 0L)
            )
        }
    }
}
