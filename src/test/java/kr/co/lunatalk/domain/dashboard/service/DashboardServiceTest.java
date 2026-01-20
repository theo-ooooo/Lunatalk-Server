package kr.co.lunatalk.domain.dashboard.service;

import kr.co.lunatalk.domain.category.repository.CategoryRepository;
import kr.co.lunatalk.domain.dashboard.dto.response.DashboardResponse;
import kr.co.lunatalk.domain.exhibition.repository.ExhibitionRepository;
import kr.co.lunatalk.domain.member.repository.MemberRepository;
import kr.co.lunatalk.domain.order.repository.OrderRepository;
import kr.co.lunatalk.domain.paymentstatistics.domain.PaymentStatistics;
import kr.co.lunatalk.domain.paymentstatistics.repository.PaymentStatisticsRepository;
import kr.co.lunatalk.domain.paymentstatistics.service.PaymentStatisticsService;
import kr.co.lunatalk.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("DashboardService 테스트")
class DashboardServiceTest {

	@Mock
	private ProductRepository productRepository;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private ExhibitionRepository exhibitionRepository;

	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private PaymentStatisticsService paymentStatisticsService;

	@Mock
	private PaymentStatisticsRepository paymentStatisticsRepository;

	@InjectMocks
	private DashboardService dashboardService;

	@Test
	@DisplayName("대시보드 통계 조회 성공")
	void 대시보드_통계_조회_성공() {
		// given
		given(productRepository.count()).willReturn(10L);
		given(memberRepository.count()).willReturn(50L);
		given(exhibitionRepository.countByStartAtLessThanEqualAndEndAtGreaterThanEqual(
			any(LocalDateTime.class), any(LocalDateTime.class))).willReturn(3L);
		given(categoryRepository.count()).willReturn(5L);

		PaymentStatistics todayStatistics = PaymentStatistics.create(
			LocalDate.now(), 100000L, 15L);
		given(paymentStatisticsService.getTodayStatistics()).willReturn(todayStatistics);

		// 최근 7일 통계 Mock
		LocalDate today = LocalDate.now();
		List<PaymentStatistics> last7DaysStatistics = List.of(
			PaymentStatistics.create(today, 100000L, 15L),
			PaymentStatistics.create(today.minusDays(1), 80000L, 12L),
			PaymentStatistics.create(today.minusDays(2), 90000L, 14L)
		);
		given(paymentStatisticsRepository.findByStatisticsDateBetweenOrderByStatisticsDateDesc(
			any(LocalDate.class), any(LocalDate.class))).willReturn(last7DaysStatistics);

		// when
		DashboardResponse response = dashboardService.getDashboardStatistics();

		// then
		assertThat(response.productCount()).isEqualTo(10L);
		assertThat(response.memberCount()).isEqualTo(50L);
		assertThat(response.todayOrderCount()).isEqualTo(15L);
		assertThat(response.activeExhibitionCount()).isEqualTo(3L);
		assertThat(response.categoryCount()).isEqualTo(5L);
		assertThat(response.todaySales()).isEqualTo(100000L);
		assertThat(response.dailyOrderCounts()).hasSize(7);
		assertThat(response.dailyOrderCounts().get(0).orderCount()).isEqualTo(15L);
	}

	@Test
	@DisplayName("빈 데이터 대시보드 통계 조회")
	void 빈_데이터_대시보드_통계_조회() {
		// given
		given(productRepository.count()).willReturn(0L);
		given(memberRepository.count()).willReturn(0L);
		given(exhibitionRepository.countByStartAtLessThanEqualAndEndAtGreaterThanEqual(
			any(LocalDateTime.class), any(LocalDateTime.class))).willReturn(0L);
		given(categoryRepository.count()).willReturn(0L);

		PaymentStatistics todayStatistics = PaymentStatistics.create(
			LocalDate.now(), 0L, 0L);
		given(paymentStatisticsService.getTodayStatistics()).willReturn(todayStatistics);

		given(paymentStatisticsRepository.findByStatisticsDateBetweenOrderByStatisticsDateDesc(
			any(LocalDate.class), any(LocalDate.class))).willReturn(List.of());

		// when
		DashboardResponse response = dashboardService.getDashboardStatistics();

		// then
		assertThat(response.productCount()).isEqualTo(0L);
		assertThat(response.memberCount()).isEqualTo(0L);
		assertThat(response.todayOrderCount()).isEqualTo(0L);
		assertThat(response.activeExhibitionCount()).isEqualTo(0L);
		assertThat(response.categoryCount()).isEqualTo(0L);
		assertThat(response.todaySales()).isEqualTo(0L);
		assertThat(response.dailyOrderCounts()).hasSize(7);
		assertThat(response.dailyOrderCounts()).allMatch(daily -> daily.orderCount() == 0L);
	}

	@Test
	@DisplayName("오늘 매출이 없는 경우")
	void 오늘_매출이_없는_경우() {
		// given
		given(productRepository.count()).willReturn(5L);
		given(memberRepository.count()).willReturn(20L);
		given(exhibitionRepository.countByStartAtLessThanEqualAndEndAtGreaterThanEqual(
			any(LocalDateTime.class), any(LocalDateTime.class))).willReturn(1L);
		given(categoryRepository.count()).willReturn(3L);

		PaymentStatistics todayStatistics = PaymentStatistics.create(
			LocalDate.now(), 0L, 0L);
		given(paymentStatisticsService.getTodayStatistics()).willReturn(todayStatistics);

		given(paymentStatisticsRepository.findByStatisticsDateBetweenOrderByStatisticsDateDesc(
			any(LocalDate.class), any(LocalDate.class))).willReturn(List.of());

		// when
		DashboardResponse response = dashboardService.getDashboardStatistics();

		// then
		assertThat(response.todaySales()).isEqualTo(0L);
		assertThat(response.todayOrderCount()).isEqualTo(0L);
		assertThat(response.dailyOrderCounts()).hasSize(7);
	}

	@Test
	@DisplayName("진행중인 기획전이 없는 경우")
	void 진행중인_기획전이_없는_경우() {
		// given
		given(productRepository.count()).willReturn(8L);
		given(memberRepository.count()).willReturn(30L);
		given(exhibitionRepository.countByStartAtLessThanEqualAndEndAtGreaterThanEqual(
			any(LocalDateTime.class), any(LocalDateTime.class))).willReturn(0L);
		given(categoryRepository.count()).willReturn(4L);

		PaymentStatistics todayStatistics = PaymentStatistics.create(
			LocalDate.now(), 50000L, 10L);
		given(paymentStatisticsService.getTodayStatistics()).willReturn(todayStatistics);

		LocalDate today = LocalDate.now();
		List<PaymentStatistics> last7DaysStatistics = List.of(
			PaymentStatistics.create(today, 50000L, 10L),
			PaymentStatistics.create(today.minusDays(1), 40000L, 8L)
		);
		given(paymentStatisticsRepository.findByStatisticsDateBetweenOrderByStatisticsDateDesc(
			any(LocalDate.class), any(LocalDate.class))).willReturn(last7DaysStatistics);

		// when
		DashboardResponse response = dashboardService.getDashboardStatistics();

		// then
		assertThat(response.activeExhibitionCount()).isEqualTo(0L);
		assertThat(response.dailyOrderCounts()).hasSize(7);
	}
}
