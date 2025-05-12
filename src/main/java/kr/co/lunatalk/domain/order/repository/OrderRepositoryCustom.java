package kr.co.lunatalk.domain.order.repository;

import kr.co.lunatalk.domain.order.domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OrderRepositoryCustom {

	Optional<Order> findByOrderWithItems(String orderNumber);

	Page<Order> findOrdersWithItemsByMemberId(Long memberId, Pageable pageable);
}
