package kr.co.lunatalk.domain.order.repository

import kr.co.lunatalk.domain.order.domain.Order
import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository : JpaRepository<Order, Long>, OrderRepositoryCustom
