package kr.co.lunatalk.domain.product.domain

import jakarta.persistence.*
import kr.co.lunatalk.domain.category.domain.Category
import kr.co.lunatalk.domain.common.domain.BaseTimeEntity
import kr.co.lunatalk.domain.product.dto.request.ProductUpdateRequest
import org.hibernate.annotations.ColumnDefault

@Entity
open class Product protected constructor() : BaseTimeEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    open var id: Long? = null
        protected set

    open var name: String? = null
        protected set

    open var price: Long? = null
        protected set

    open var quantity: Int? = null
        protected set

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @ColumnDefault("'active'")
    open var status: ProductStatus = ProductStatus.ACTIVE
        protected set

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    @ColumnDefault("'hidden'")
    open var visibility: ProductVisibility = ProductVisibility.HIDDEN
        protected set

    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL], orphanRemoval = true)
    open var productColor: MutableList<ProductColor> = mutableListOf()
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    open var category: Category? = null

    private constructor(
        name: String,
        price: Long,
        quantity: Int,
        status: ProductStatus,
        visibility: ProductVisibility
    ) : this() {
        this.name = name
        this.price = price
        this.quantity = quantity
        this.status = status
        this.visibility = visibility
    }

    fun updateProduct(request: ProductUpdateRequest) {
        request.name?.let { this.name = it }
        request.price?.let { this.price = it }
        request.quantity?.let { this.quantity = it }
        request.visibility?.let { this.visibility = it }

        if (request.colors.isNotEmpty()) {
            clearProductColor()
            request.colors.forEach { color ->
                addProductColor(ProductColor.createProductColor(this, color))
            }
        }
    }

    fun addProductColor(productColor: ProductColor) {
        this.productColor.add(productColor)
    }

    fun clearProductColor() {
        this.productColor.clear()
    }

    fun deleteProduct() {
        if (this.visibility == ProductVisibility.VISIBLE) {
            this.visibility = ProductVisibility.HIDDEN
        }
        if (this.status == ProductStatus.ACTIVE) {
            this.status = ProductStatus.DELETED
        }
    }

    fun minusProductQuantity(quantity: Int) {
        val currentQuantity = this.quantity ?: return
        if (currentQuantity >= quantity) {
            this.quantity = currentQuantity - quantity
        }
    }

    companion object {
        fun createProduct(
            name: String,
            price: Long,
            quantity: Int,
            status: ProductStatus,
            visibility: ProductVisibility
        ): Product {
            return Product(name, price, quantity, status, visibility)
        }
    }
}
