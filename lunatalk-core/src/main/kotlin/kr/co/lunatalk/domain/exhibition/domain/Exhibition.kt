package kr.co.lunatalk.domain.exhibition.domain

import jakarta.persistence.*
import kr.co.lunatalk.domain.common.domain.BaseTimeEntity
import org.hibernate.annotations.ColumnDefault
import java.time.LocalDateTime

@Entity
open class Exhibition protected constructor() : BaseTimeEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null
        protected set

    @Column(nullable = false)
    open var title: String = ""
        protected set

    open var description: String? = null
        protected set

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'VISIBLE'")
    open var visibility: ExhibitionVisibility? = null
        protected set

    @OneToMany(mappedBy = "exhibition", cascade = [CascadeType.ALL], orphanRemoval = true)
    open var exhibitionProducts: MutableList<ExhibitionProduct> = mutableListOf()
        protected set

    @Column(nullable = false)
    open var startAt: LocalDateTime = LocalDateTime.now()
        protected set

    open var endAt: LocalDateTime? = null
        protected set

    constructor(
        title: String,
        description: String?,
        visibility: ExhibitionVisibility?,
        startAt: LocalDateTime,
        endAt: LocalDateTime?
    ) : this() {
        this.title = title
        this.description = description
        this.visibility = visibility
        this.startAt = startAt
        this.endAt = endAt
    }

    fun updateExhibition(
        title: String,
        description: String?,
        visibility: ExhibitionVisibility?,
        startAt: LocalDateTime,
        endAt: LocalDateTime?
    ) {
        this.title = title
        this.description = description
        this.visibility = visibility ?: ExhibitionVisibility.HIDDEN
        this.startAt = startAt
        this.endAt = endAt
    }

    fun addProducts(products: List<ExhibitionProduct>) {
        this.exhibitionProducts.addAll(products)
    }

    fun addProduct(product: ExhibitionProduct) {
        this.exhibitionProducts.add(product)
    }

    companion object {
        fun createExhibition(
            title: String,
            description: String?,
            visibility: ExhibitionVisibility?,
            startAt: LocalDateTime,
            endAt: LocalDateTime?
        ): Exhibition {
            return Exhibition(title, description, visibility, startAt, endAt)
        }
    }
}
