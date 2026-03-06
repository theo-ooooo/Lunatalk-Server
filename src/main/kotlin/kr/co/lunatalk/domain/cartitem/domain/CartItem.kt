package kr.co.lunatalk.domain.cartitem.domain

import jakarta.persistence.*
import kr.co.lunatalk.domain.common.domain.BaseTimeEntity
import kr.co.lunatalk.domain.member.domain.Member
import kr.co.lunatalk.domain.product.domain.Product

@Entity
open class CartItem protected constructor() : BaseTimeEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    open var member: Member? = null
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    open var product: Product? = null
        protected set

    open var quantity: Int = 0
        protected set

    constructor(member: Member, product: Product, quantity: Int) : this() {
        this.member = member
        this.product = product
        this.quantity = quantity
    }

    fun updateQuantity(quantity: Int) {
        this.quantity += quantity
    }

    companion object {
        fun createCartItem(member: Member, product: Product, quantity: Int?): CartItem {
            return CartItem(member, product, quantity ?: 1)
        }
    }
}
