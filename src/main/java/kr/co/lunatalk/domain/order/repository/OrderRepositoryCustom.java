package kr.co.lunatalk.domain.order.repository;

import kr.co.lunatalk.domain.order.domain.Order;

public interface OrderRepositoryCustom {

	Order findByOrderWithItems(String orderNumber);
}
