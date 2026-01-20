package kr.co.lunatalk.domain.payment.repository;

import kr.co.lunatalk.domain.payment.domain.PaymentStatus;

import java.time.LocalDateTime;

public interface PaymentRepositoryCustom {
	Long sumAmountByStatusAndApprovedAtBetween(
		PaymentStatus status,
		LocalDateTime start,
		LocalDateTime end
	);

	Long countByStatusAndApprovedAtBetween(
		PaymentStatus status,
		LocalDateTime start,
		LocalDateTime end
	);
}

