package kr.co.lunatalk.domain.product.repository

import kr.co.lunatalk.domain.product.domain.Product
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.Optional

interface ProductRepositoryCustom {
    fun findProductById(productId: Long): Optional<Product>
    fun findAllProductsByProductIds(productIds: List<Long>): List<Product>
    fun findAllProductDtoByIdsWithJoin(productIds: List<Long>): List<Product>
    fun findAll(productName: String?, pageable: Pageable): Page<Product>
}
