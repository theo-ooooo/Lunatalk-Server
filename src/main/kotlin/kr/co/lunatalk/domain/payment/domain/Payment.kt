package kr.co.lunatalk.domain.payment.domain

import jakarta.persistence.*
import kr.co.lunatalk.domain.common.domain.BaseTimeEntity
import kr.co.lunatalk.domain.order.domain.Order
import java.time.LocalDateTime

@Entity
@Table(name = "payments")
open class Payment protected constructor() : BaseTimeEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    open var order: Order? = null
        protected set

    @Column(nullable = false, unique = true)
    open var paymentKey: String? = null
        protected set

    @Column(nullable = false)
    open var orderNumber: String? = null
        protected set

    @Column(nullable = false)
    open var amount: Long? = null
        protected set

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    open var status: PaymentStatus? = null
        protected set

    open var method: String? = null
        protected set

    open var approvedAt: LocalDateTime? = null
        protected set

    private constructor(
        order: Order?,
        paymentKey: String?,
        orderNumber: String?,
        amount: Long?,
        status: PaymentStatus?,
        method: String?,
        approvedAt: LocalDateTime?
    ) : this() {
        this.order = order
        this.paymentKey = paymentKey
        this.orderNumber = orderNumber
        this.amount = amount
        this.status = status
        this.method = method
        this.approvedAt = approvedAt
    }

    fun cancel() {
        this.status = PaymentStatus.CANCELLED
    }

    fun fail() {
        this.status = PaymentStatus.FAILED
    }

    companion object {
        fun success(
            order: Order,
            paymentKey: String,
            orderNumber: String,
            amount: Long,
            method: String?,
            approvedAt: LocalDateTime?
        ): Payment {
            return Payment(
                order = order,
                paymentKey = paymentKey,
                orderNumber = orderNumber,
                amount = amount,
                status = PaymentStatus.SUCCESS,
                method = method,
                approvedAt = approvedAt
            )
        }
    }
}
