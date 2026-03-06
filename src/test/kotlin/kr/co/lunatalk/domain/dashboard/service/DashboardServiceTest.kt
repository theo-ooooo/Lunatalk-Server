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
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.ArgumentMatchers.any
import org.mockito.BDDMockito.given
import org.mockito.junit.jupiter.MockitoExtension
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

    @InjectMocks
    lateinit var dashboardService: DashboardService

    @Test
    @DisplayName("대시보드 통계 조회 성공")
    fun `대시보드 통계 조회 성공`() {
        // given
        given(productRepository.count()).willReturn(10L)
        given(memberRepository.count()).willReturn(50L)
        given(
            exhibitionRepository.countByStartAtLessThanEqualAndEndAtGreaterThanEqual(
                any(LocalDateTime::class.java), any(LocalDateTime::class.java)
            )
        ).willReturn(3L)
        given(categoryRepository.count()).willReturn(5L)

        val todayStatistics = PaymentStatistics.create(LocalDate.now(), 100000L, 15L)
        given(paymentStatisticsService.getTodayStatistics()).willReturn(todayStatistics)

        val today = LocalDate.now()
        val last7DaysStatistics = listOf(
            PaymentStatistics.create(today, 100000L, 15L),
            PaymentStatistics.create(today.minusDays(1), 80000L, 12L),
            PaymentStatistics.create(today.minusDays(2), 90000L, 14L)
        )
        given(
            paymentStatisticsRepository.findByStatisticsDateBetweenOrderByStatisticsDateDesc(
                any(LocalDate::class.java), any(LocalDate::class.java)
            )
        ).willReturn(last7DaysStatistics)

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
        given(productRepository.count()).willReturn(0L)
        given(memberRepository.count()).willReturn(0L)
        given(
            exhibitionRepository.countByStartAtLessThanEqualAndEndAtGreaterThanEqual(
                any(LocalDateTime::class.java), any(LocalDateTime::class.java)
            )
        ).willReturn(0L)
        given(categoryRepository.count()).willReturn(0L)

        val todayStatistics = PaymentStatistics.create(LocalDate.now(), 0L, 0L)
        given(paymentStatisticsService.getTodayStatistics()).willReturn(todayStatistics)

        given(
            paymentStatisticsRepository.findByStatisticsDateBetweenOrderByStatisticsDateDesc(
                any(LocalDate::class.java), any(LocalDate::class.java)
            )
        ).willReturn(listOf())

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
        given(productRepository.count()).willReturn(5L)
        given(memberRepository.count()).willReturn(20L)
        given(
            exhibitionRepository.countByStartAtLessThanEqualAndEndAtGreaterThanEqual(
                any(LocalDateTime::class.java), any(LocalDateTime::class.java)
            )
        ).willReturn(1L)
        given(categoryRepository.count()).willReturn(3L)

        val todayStatistics = PaymentStatistics.create(LocalDate.now(), 0L, 0L)
        given(paymentStatisticsService.getTodayStatistics()).willReturn(todayStatistics)

        given(
            paymentStatisticsRepository.findByStatisticsDateBetweenOrderByStatisticsDateDesc(
                any(LocalDate::class.java), any(LocalDate::class.java)
            )
        ).willReturn(listOf())

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
        given(productRepository.count()).willReturn(8L)
        given(memberRepository.count()).willReturn(30L)
        given(
            exhibitionRepository.countByStartAtLessThanEqualAndEndAtGreaterThanEqual(
                any(LocalDateTime::class.java), any(LocalDateTime::class.java)
            )
        ).willReturn(0L)
        given(categoryRepository.count()).willReturn(4L)

        val todayStatistics = PaymentStatistics.create(LocalDate.now(), 50000L, 10L)
        given(paymentStatisticsService.getTodayStatistics()).willReturn(todayStatistics)

        val today = LocalDate.now()
        val last7DaysStatistics = listOf(
            PaymentStatistics.create(today, 50000L, 10L),
            PaymentStatistics.create(today.minusDays(1), 40000L, 8L)
        )
        given(
            paymentStatisticsRepository.findByStatisticsDateBetweenOrderByStatisticsDateDesc(
                any(LocalDate::class.java), any(LocalDate::class.java)
            )
        ).willReturn(last7DaysStatistics)

        // when
        val response: DashboardResponse = dashboardService.getDashboardStatistics()

        // then
        assertThat(response.activeExhibitionCount).isEqualTo(0L)
        assertThat(response.dailyOrderCounts).hasSize(7)
    }
}
