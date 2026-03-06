package kr.co.lunatalk.domain.productlike.domain

import jakarta.persistence.*
import kr.co.lunatalk.domain.common.domain.BaseTimeEntity
import kr.co.lunatalk.domain.member.domain.Member
import kr.co.lunatalk.domain.product.domain.Product

@Entity
@Table(
    name = "product_likes",
    uniqueConstraints = [UniqueConstraint(columnNames = ["member_id", "product_id"])]
)
open class ProductLike protected constructor() : BaseTimeEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    open var member: Member? = null
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    open var product: Product? = null
        protected set

    private constructor(member: Member, product: Product) : this() {
        this.member = member
        this.product = product
    }

    companion object {
        fun create(member: Member, product: Product): ProductLike {
            return ProductLike(member, product)
        }
    }
}
