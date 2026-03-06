package kr.co.lunatalk.domain.order.repository

import kr.co.lunatalk.domain.order.domain.Order
import kr.co.lunatalk.domain.order.domain.OrderStatus
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface OrderRepository : JpaRepository<Order, Long>, OrderRepositoryCustom {

    fun countByCreatedAtBetween(start: LocalDateTime, end: LocalDateTime): Long

    fun countByStatusAndCreatedAtBetween(status: OrderStatus, start: LocalDateTime, end: LocalDateTime): Long
}
