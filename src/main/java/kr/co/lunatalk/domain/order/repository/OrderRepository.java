package kr.co.lunatalk.domain.order.repository;

import kr.co.lunatalk.domain.order.domain.Order;
import kr.co.lunatalk.domain.order.domain.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {
	long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

	long countByStatusAndCreatedAtBetween(OrderStatus status, LocalDateTime start, LocalDateTime end);
}
