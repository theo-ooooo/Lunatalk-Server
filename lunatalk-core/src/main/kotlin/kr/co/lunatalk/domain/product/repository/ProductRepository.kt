package kr.co.lunatalk.domain.product.repository

import kr.co.lunatalk.domain.product.domain.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ProductRepository : JpaRepository<Product, Long>, ProductRepositoryCustom {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Product p SET p.category = null WHERE p.category.id = :categoryId")
    fun bulkClearCategory(@Param("categoryId") categoryId: Long)

    @Modifying(flushAutomatically = true)
    @Query("""
        UPDATE Product p
        SET p.quantity = p.quantity - :quantity
        WHERE p.id = :productId
          AND p.quantity >= :quantity
    """)
    fun decreaseQuantityIfAvailable(
        @Param("productId") productId: Long,
        @Param("quantity") quantity: Int
    ): Int
}
