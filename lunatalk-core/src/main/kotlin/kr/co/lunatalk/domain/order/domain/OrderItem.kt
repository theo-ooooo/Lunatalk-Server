package kr.co.lunatalk.domain.order.domain

import jakarta.persistence.*
import kr.co.lunatalk.domain.common.domain.BaseTimeEntity

@Entity
open class OrderItem protected constructor() : BaseTimeEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    open var order: Order? = null
        protected set

    @Column(nullable = false)
    open var productId: Long? = null
        protected set

    @Column(nullable = false)
    open var productName: String? = null
        protected set

    @Column(nullable = false)
    open var price: Long? = null
        protected set

    @Column(nullable = false)
    open var quantity: Int? = null
        protected set

    @Column(nullable = false)
    open var totalPrice: Long? = null
        protected set

    @Embedded
    open var optionSnapshot: OptionSnapshot? = null
        protected set

    private constructor(
        order: Order?,
        productId: Long?,
        productName: String?,
        price: Long?,
        quantity: Int?,
        totalPrice: Long?,
        optionSnapshot: OptionSnapshot?
    ) : this() {
        this.order = order
        this.productId = productId
        this.productName = productName
        this.price = price
        this.quantity = quantity
        this.totalPrice = totalPrice
        this.optionSnapshot = optionSnapshot
    }

    companion object {
        fun createOrderItem(
            order: Order?,
            productId: Long?,
            productName: String?,
            price: Long?,
            quantity: Int?,
            totalPrice: Long?,
            optionSnapshot: OptionSnapshot?
        ): OrderItem {
            return OrderItem(
                order = order,
                productId = productId,
                productName = productName,
                price = price,
                quantity = quantity,
                totalPrice = totalPrice,
                optionSnapshot = optionSnapshot
            )
        }
    }
}
