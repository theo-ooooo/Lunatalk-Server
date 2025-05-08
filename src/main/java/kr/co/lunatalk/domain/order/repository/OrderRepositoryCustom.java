package kr.co.lunatalk.domain.order.repository;

import kr.co.lunatalk.domain.order.domain.Order;

import java.util.Optional;

public interface OrderRepositoryCustom {

	Optional<Order> findByOrderWithItems(String orderNumber);
}
