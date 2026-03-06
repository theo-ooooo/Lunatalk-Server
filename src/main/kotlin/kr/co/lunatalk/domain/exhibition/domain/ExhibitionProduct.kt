package kr.co.lunatalk.domain.exhibition.domain

import jakarta.persistence.*
import kr.co.lunatalk.domain.common.domain.BaseTimeEntity
import kr.co.lunatalk.domain.product.domain.Product

@Entity
open class ExhibitionProduct protected constructor() : BaseTimeEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exhibition_id")
    open var exhibition: Exhibition? = null
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    open var product: Product? = null
        protected set

    open var sortOrder: Int = 0
        protected set

    constructor(exhibition: Exhibition, product: Product, sortOrder: Int) : this() {
        this.exhibition = exhibition
        this.product = product
        this.sortOrder = sortOrder
    }

    companion object {
        fun createExhibitionProduct(exhibition: Exhibition, product: Product, sortOrder: Int): ExhibitionProduct {
            return ExhibitionProduct(exhibition, product, sortOrder)
        }
    }
}
