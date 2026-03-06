package kr.co.lunatalk.domain.product.domain

import jakarta.persistence.*
import kr.co.lunatalk.domain.common.domain.BaseTimeEntity

@Entity
open class ProductColor protected constructor() : BaseTimeEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    open var product: Product? = null
        protected set

    open var color: String? = null
        protected set

    private constructor(product: Product, color: String) : this() {
        this.product = product
        this.color = color
    }

    fun updateProductColor(color: String) {
        this.color = color
    }

    companion object {
        fun createProductColor(product: Product, color: String): ProductColor {
            return ProductColor(product, color)
        }
    }
}
