package kr.co.lunatalk.domain.payment.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.lunatalk.domain.payment.domain.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import static kr.co.lunatalk.domain.payment.domain.QPayment.payment;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Long sumAmountByStatusAndApprovedAtBetween(
		PaymentStatus status,
		LocalDateTime start,
		LocalDateTime end
	) {
		Long result = queryFactory
			.select(payment.amount.sum())
			.from(payment)
			.where(
				payment.status.eq(status),
				payment.approvedAt.isNotNull(),
				payment.approvedAt.between(start, end)
			)
			.fetchOne();

		return result != null ? result : 0L;
	}

	@Override
	public Long countByStatusAndApprovedAtBetween(
		PaymentStatus status,
		LocalDateTime start,
		LocalDateTime end
	) {
		Long result = queryFactory
			.select(payment.count())
			.from(payment)
			.where(
				payment.status.eq(status),
				payment.approvedAt.isNotNull(),
				payment.approvedAt.between(start, end)
			)
			.fetchOne();

		return result != null ? result : 0L;
	}
}

