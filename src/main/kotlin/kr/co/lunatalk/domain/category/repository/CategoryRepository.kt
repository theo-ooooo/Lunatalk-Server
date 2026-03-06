package kr.co.lunatalk.domain.category.repository

import kr.co.lunatalk.domain.category.domain.Category
import kr.co.lunatalk.domain.category.domain.CategoryStatus
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface CategoryRepository : JpaRepository<Category, Long>, CategoryRepositoryCustom {
    fun findByName(name: String): Optional<Category>
    fun existsByName(name: String): Boolean
    fun findAllByStatus(status: CategoryStatus): List<Category>
    fun findByIdAndStatus(id: Long, status: CategoryStatus): Optional<Category>
}
