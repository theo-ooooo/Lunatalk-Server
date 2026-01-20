package kr.co.lunatalk.domain.dashboard.service;

import kr.co.lunatalk.domain.category.repository.CategoryRepository;
import kr.co.lunatalk.domain.dashboard.dto.response.DailyOrderCountResponse;
import kr.co.lunatalk.domain.dashboard.dto.response.DashboardResponse;
import kr.co.lunatalk.domain.exhibition.repository.ExhibitionRepository;
import kr.co.lunatalk.domain.member.repository.MemberRepository;
import kr.co.lunatalk.domain.order.repository.OrderRepository;
import kr.co.lunatalk.domain.paymentstatistics.domain.PaymentStatistics;
import kr.co.lunatalk.domain.paymentstatistics.repository.PaymentStatisticsRepository;
import kr.co.lunatalk.domain.paymentstatistics.service.PaymentStatisticsService;
import kr.co.lunatalk.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

	private final ProductRepository productRepository;
	private final MemberRepository memberRepository;
	private final OrderRepository orderRepository;
	private final ExhibitionRepository exhibitionRepository;
	private final CategoryRepository categoryRepository;
	private final PaymentStatisticsService paymentStatisticsService;
	private final PaymentStatisticsRepository paymentStatisticsRepository;

	public DashboardResponse getDashboardStatistics() {
		Long productCount = productRepository.count();
		Long memberCount = memberRepository.count();
		
		// 통계 테이블에서 오늘 통계 조회
		PaymentStatistics todayStatistics = paymentStatisticsService.getTodayStatistics();
		Long todayOrderCount = todayStatistics.getOrderCount();
		Long todaySales = todayStatistics.getTotalSales();
		
		Long activeExhibitionCount = exhibitionRepository.countByStartAtLessThanEqualAndEndAtGreaterThanEqual(
			LocalDateTime.now(), LocalDateTime.now());
		
		Long categoryCount = categoryRepository.count();
		
		// 최근 7일 일별 주문 수 조회
		List<DailyOrderCountResponse> dailyOrderCounts = getDailyOrderCountsForLast7Days();
		
		return new DashboardResponse(
			productCount,
			memberCount,
			todayOrderCount,
			activeExhibitionCount,
			categoryCount,
			todaySales,
			dailyOrderCounts
		);
	}

	private List<DailyOrderCountResponse> getDailyOrderCountsForLast7Days() {
		LocalDate today = LocalDate.now();
		LocalDate sevenDaysAgo = today.minusDays(6); // 오늘 포함 7일

		// 최근 7일 통계 조회
		List<PaymentStatistics> statistics = paymentStatisticsRepository
			.findByStatisticsDateBetweenOrderByStatisticsDateDesc(sevenDaysAgo, today);

		// 날짜별로 맵 생성
		Map<LocalDate, Long> statisticsMap = statistics.stream()
			.collect(Collectors.toMap(
				PaymentStatistics::getStatisticsDate,
				PaymentStatistics::getOrderCount
			));

		// 최근 7일 날짜 리스트 생성 (오늘부터 6일 전까지)
		return IntStream.range(0, 7)
			.mapToObj(i -> today.minusDays(i))
			.map(date -> new DailyOrderCountResponse(
				date,
				statisticsMap.getOrDefault(date, 0L)
			))
			.toList();
	}
}

