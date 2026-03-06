package kr.co.lunatalk.domain.dashboard.service

import kr.co.lunatalk.domain.category.repository.CategoryRepository
import kr.co.lunatalk.domain.dashboard.dto.response.DashboardResponse
import kr.co.lunatalk.domain.exhibition.repository.ExhibitionRepository
import kr.co.lunatalk.domain.member.repository.MemberRepository
import kr.co.lunatalk.domain.order.repository.OrderRepository
import kr.co.lunatalk.domain.paymentstatistics.domain.PaymentStatistics
import kr.co.lunatalk.domain.paymentstatistics.repository.PaymentStatisticsRepository
import kr.co.lunatalk.domain.paymentstatistics.service.PaymentStatisticsService
import kr.co.lunatalk.domain.product.repository.ProductRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import java.time.LocalDate
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
@DisplayName("DashboardService 테스트")
class DashboardServiceTest {

    @Mock
    lateinit var productRepository: ProductRepository

    @Mock
    lateinit var memberRepository: MemberRepository

    @Mock
    lateinit var orderRepository: OrderRepository

    @Mock
    lateinit var exhibitionRepository: ExhibitionRepository

    @Mock
    lateinit var categoryRepository: CategoryRepository

    @Mock
    lateinit var paymentStatisticsService: PaymentStatisticsService

    @Mock
    lateinit var paymentStatisticsRepository: PaymentStatisticsRepository

    lateinit var dashboardService: DashboardService

    @BeforeEach
    fun setUp() {
        dashboardService = DashboardService(
            productRepository, memberRepository, orderRepository,
            exhibitionRepository, categoryRepository,
            paymentStatisticsService, paymentStatisticsRepository
        )
    }

    @Test
    @DisplayName("대시보드 통계 조회 성공")
    fun `대시보드 통계 조회 성공`() {
        // given
        whenever(productRepository.count()).thenReturn(10L)
        whenever(memberRepository.count()).thenReturn(50L)
        whenever(
            exhibitionRepository.countByStartAtLessThanEqualAndEndAtGreaterThanEqual(
                any<LocalDateTime>(), any<LocalDateTime>()
            )
        ).thenReturn(3L)
        whenever(categoryRepository.count()).thenReturn(5L)

        val todayStatistics = PaymentStatistics.create(LocalDate.now(), 100000L, 15L)
        whenever(paymentStatisticsService.getTodayStatistics()).thenReturn(todayStatistics)

        val today = LocalDate.now()
        val last7DaysStatistics = listOf(
            PaymentStatistics.create(today, 100000L, 15L),
            PaymentStatistics.create(today.minusDays(1), 80000L, 12L),
            PaymentStatistics.create(today.minusDays(2), 90000L, 14L)
        )
        whenever(
            paymentStatisticsRepository.findByStatisticsDateBetweenOrderByStatisticsDateDesc(
                any<LocalDate>(), any<LocalDate>()
            )
        ).thenReturn(last7DaysStatistics)

        // when
        val response: DashboardResponse = dashboardService.getDashboardStatistics()

        // then
        assertThat(response.productCount).isEqualTo(10L)
        assertThat(response.memberCount).isEqualTo(50L)
        assertThat(response.todayOrderCount).isEqualTo(15L)
        assertThat(response.activeExhibitionCount).isEqualTo(3L)
        assertThat(response.categoryCount).isEqualTo(5L)
        assertThat(response.todaySales).isEqualTo(100000L)
        assertThat(response.dailyOrderCounts).hasSize(7)
        assertThat(response.dailyOrderCounts[0].orderCount).isEqualTo(15L)
    }

    @Test
    @DisplayName("빈 데이터 대시보드 통계 조회")
    fun `빈 데이터 대시보드 통계 조회`() {
        // given
        whenever(productRepository.count()).thenReturn(0L)
        whenever(memberRepository.count()).thenReturn(0L)
        whenever(
            exhibitionRepository.countByStartAtLessThanEqualAndEndAtGreaterThanEqual(
                any<LocalDateTime>(), any<LocalDateTime>()
            )
        ).thenReturn(0L)
        whenever(categoryRepository.count()).thenReturn(0L)

        val todayStatistics = PaymentStatistics.create(LocalDate.now(), 0L, 0L)
        whenever(paymentStatisticsService.getTodayStatistics()).thenReturn(todayStatistics)

        whenever(
            paymentStatisticsRepository.findByStatisticsDateBetweenOrderByStatisticsDateDesc(
                any<LocalDate>(), any<LocalDate>()
            )
        ).thenReturn(listOf())

        // when
        val response: DashboardResponse = dashboardService.getDashboardStatistics()

        // then
        assertThat(response.productCount).isEqualTo(0L)
        assertThat(response.memberCount).isEqualTo(0L)
        assertThat(response.todayOrderCount).isEqualTo(0L)
        assertThat(response.activeExhibitionCount).isEqualTo(0L)
        assertThat(response.categoryCount).isEqualTo(0L)
        assertThat(response.todaySales).isEqualTo(0L)
        assertThat(response.dailyOrderCounts).hasSize(7)
        assertThat(response.dailyOrderCounts).allMatch { it.orderCount == 0L }
    }

    @Test
    @DisplayName("오늘 매출이 없는 경우")
    fun `오늘 매출이 없는 경우`() {
        // given
        whenever(productRepository.count()).thenReturn(5L)
        whenever(memberRepository.count()).thenReturn(20L)
        whenever(
            exhibitionRepository.countByStartAtLessThanEqualAndEndAtGreaterThanEqual(
                any<LocalDateTime>(), any<LocalDateTime>()
            )
        ).thenReturn(1L)
        whenever(categoryRepository.count()).thenReturn(3L)

        val todayStatistics = PaymentStatistics.create(LocalDate.now(), 0L, 0L)
        whenever(paymentStatisticsService.getTodayStatistics()).thenReturn(todayStatistics)

        whenever(
            paymentStatisticsRepository.findByStatisticsDateBetweenOrderByStatisticsDateDesc(
                any<LocalDate>(), any<LocalDate>()
            )
        ).thenReturn(listOf())

        // when
        val response: DashboardResponse = dashboardService.getDashboardStatistics()

        // then
        assertThat(response.todaySales).isEqualTo(0L)
        assertThat(response.todayOrderCount).isEqualTo(0L)
        assertThat(response.dailyOrderCounts).hasSize(7)
    }

    @Test
    @DisplayName("진행중인 기획전이 없는 경우")
    fun `진행중인 기획전이 없는 경우`() {
        // given
        whenever(productRepository.count()).thenReturn(8L)
        whenever(memberRepository.count()).thenReturn(30L)
        whenever(
            exhibitionRepository.countByStartAtLessThanEqualAndEndAtGreaterThanEqual(
                any<LocalDateTime>(), any<LocalDateTime>()
            )
        ).thenReturn(0L)
        whenever(categoryRepository.count()).thenReturn(4L)

        val todayStatistics = PaymentStatistics.create(LocalDate.now(), 50000L, 10L)
        whenever(paymentStatisticsService.getTodayStatistics()).thenReturn(todayStatistics)

        val today = LocalDate.now()
        val last7DaysStatistics = listOf(
            PaymentStatistics.create(today, 50000L, 10L),
            PaymentStatistics.create(today.minusDays(1), 40000L, 8L)
        )
        whenever(
            paymentStatisticsRepository.findByStatisticsDateBetweenOrderByStatisticsDateDesc(
                any<LocalDate>(), any<LocalDate>()
            )
        ).thenReturn(last7DaysStatistics)

        // when
        val response: DashboardResponse = dashboardService.getDashboardStatistics()

        // then
        assertThat(response.activeExhibitionCount).isEqualTo(0L)
        assertThat(response.dailyOrderCounts).hasSize(7)
    }
}
