package kr.co.lunatalk.domain.payment.repository;

import kr.co.lunatalk.domain.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long>, PaymentRepositoryCustom {
	Optional<Payment> findByOrderId(Long orderId);
}
