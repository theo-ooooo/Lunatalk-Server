package kr.co.lunatalk.domain.payment.repository

import kr.co.lunatalk.domain.payment.domain.Payment
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface PaymentRepository : JpaRepository<Payment, Long>, PaymentRepositoryCustom {

    fun findByOrder_OrderNumber(orderNumber: String): Optional<Payment>

    fun findByPaymentKey(paymentKey: String): Optional<Payment>
}
