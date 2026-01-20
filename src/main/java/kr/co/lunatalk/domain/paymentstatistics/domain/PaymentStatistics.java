package kr.co.lunatalk.domain.paymentstatistics.domain;

import jakarta.persistence.*;
import kr.co.lunatalk.domain.common.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "payment_statistics", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"statistics_date"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentStatistics extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private LocalDate statisticsDate;

	@Column(nullable = false)
	private Long totalSales;

	@Column(nullable = false)
	private Long orderCount;

	@Builder
	private PaymentStatistics(LocalDate statisticsDate, Long totalSales, Long orderCount) {
		this.statisticsDate = statisticsDate;
		this.totalSales = totalSales;
		this.orderCount = orderCount;
	}

	public static PaymentStatistics create(LocalDate statisticsDate, Long totalSales, Long orderCount) {
		return PaymentStatistics.builder()
			.statisticsDate(statisticsDate)
			.totalSales(totalSales != null ? totalSales : 0L)
			.orderCount(orderCount != null ? orderCount : 0L)
			.build();
	}

	public void updateStatistics(Long totalSales, Long orderCount) {
		this.totalSales = totalSales != null ? totalSales : 0L;
		this.orderCount = orderCount != null ? orderCount : 0L;
	}
}

