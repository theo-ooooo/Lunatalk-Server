package kr.co.lunatalk.domain.category.domain

import jakarta.persistence.*
import kr.co.lunatalk.domain.common.domain.BaseTimeEntity
import kr.co.lunatalk.domain.product.domain.Product
import org.hibernate.annotations.ColumnDefault

@Entity
open class Category protected constructor() : BaseTimeEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    open var id: Long? = null
        protected set

    @Column(name = "name", unique = true, nullable = false)
    open var name: String? = null
        protected set

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'ACTIVE'")
    open var status: CategoryStatus? = null
        protected set

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'VISIBLE'")
    open var visibility: CategoryVisibility? = null
        protected set

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "category")
    open var products: MutableList<Product> = mutableListOf()
        protected set

    private constructor(name: String, status: CategoryStatus, visibility: CategoryVisibility) : this() {
        this.name = name
        this.status = status
        this.visibility = visibility
    }

    fun updateName(name: String) {
        this.name = name
    }

    fun deleteStatus() {
        this.status = CategoryStatus.DELETED
    }

    fun updateVisibility(visibility: CategoryVisibility) {
        this.visibility = visibility
    }

    fun addProduct(product: Product) {
        this.products.add(product)
        product.category = this
    }

    fun removeProduct(product: Product) {
        this.products.remove(product)
        product.category = null
    }

    companion object {
        fun createCategory(name: String, visibility: CategoryVisibility): Category {
            return Category(name, CategoryStatus.ACTIVE, visibility)
        }
    }
}
