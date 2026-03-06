package kr.co.lunatalk.domain.order.domain

import jakarta.persistence.*
import kr.co.lunatalk.domain.common.domain.BaseTimeEntity
import kr.co.lunatalk.domain.delivery.domain.Delivery
import kr.co.lunatalk.domain.member.domain.Member

@Entity
@Table(name = "orders")
open class Order protected constructor() : BaseTimeEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null
        protected set

    @Column(nullable = false, unique = true)
    open var orderNumber: String? = null
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    open var member: Member? = null
        protected set

    open var totalPrice: Long? = null
        protected set

    @Column(length = 30)
    @Enumerated(EnumType.STRING)
    open var status: OrderStatus? = null
        protected set

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], orphanRemoval = true)
    open var orderItems: MutableList<OrderItem> = mutableListOf()
        protected set

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL])
    open var deliverys: MutableList<Delivery> = mutableListOf()
        protected set

    private constructor(
        orderNumber: String?,
        member: Member?,
        totalPrice: Long?,
        status: OrderStatus?
    ) : this() {
        this.orderNumber = orderNumber
        this.member = member
        this.totalPrice = totalPrice
        this.status = status
    }

    fun addOrderItem(orderItem: OrderItem) {
        orderItems.add(orderItem)
    }

    fun updateTotalPrice(totalPrice: Long) {
        this.totalPrice = totalPrice
    }

    fun updateStatus(status: OrderStatus) {
        this.status = status
    }

    companion object {
        fun createOrder(orderNumber: String, member: Member, totalPrice: Long): Order {
            return Order(
                orderNumber = orderNumber,
                member = member,
                totalPrice = totalPrice,
                status = OrderStatus.ORDERED
            )
        }
    }
}
