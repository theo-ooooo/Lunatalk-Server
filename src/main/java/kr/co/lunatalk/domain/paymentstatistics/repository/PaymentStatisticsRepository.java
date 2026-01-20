package kr.co.lunatalk.domain.paymentstatistics.repository;

import kr.co.lunatalk.domain.paymentstatistics.domain.PaymentStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PaymentStatisticsRepository extends JpaRepository<PaymentStatistics, Long> {
	Optional<PaymentStatistics> findByStatisticsDate(LocalDate date);

	List<PaymentStatistics> findByStatisticsDateBetweenOrderByStatisticsDateDesc(LocalDate start, LocalDate end);
}

