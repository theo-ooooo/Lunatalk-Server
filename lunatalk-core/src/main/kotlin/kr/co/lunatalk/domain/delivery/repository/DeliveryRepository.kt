package kr.co.lunatalk.domain.delivery.repository

import kr.co.lunatalk.domain.delivery.domain.Delivery
import org.springframework.data.jpa.repository.JpaRepository

interface DeliveryRepository : JpaRepository<Delivery, Long>, DeliveryRepositoryCustom
