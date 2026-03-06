package kr.co.lunatalk.domain.category.repository

import kr.co.lunatalk.domain.category.domain.Category
import java.util.Optional

interface CategoryRepositoryCustom {
    fun findWithProducts(categoryId: Long): Optional<Category>
    fun findAllWithProducts(): List<Category>
}
