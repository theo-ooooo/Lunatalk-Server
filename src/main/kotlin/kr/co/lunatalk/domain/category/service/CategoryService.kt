package kr.co.lunatalk.domain.category.service

import kr.co.lunatalk.domain.category.domain.Category
import kr.co.lunatalk.domain.category.domain.CategoryStatus
import kr.co.lunatalk.domain.category.domain.CategoryVisibility
import kr.co.lunatalk.domain.category.dto.request.CategoryAddProductRequest
import kr.co.lunatalk.domain.category.dto.request.CategoryCreateRequest
import kr.co.lunatalk.domain.category.dto.request.CategoryUpdateRequest
import kr.co.lunatalk.domain.category.dto.response.CategoryAddProductResponse
import kr.co.lunatalk.domain.category.dto.response.CategoryCreateResponse
import kr.co.lunatalk.domain.category.dto.response.CategoryProductResponse
import kr.co.lunatalk.domain.category.dto.response.CategoryResponse
import kr.co.lunatalk.domain.category.repository.CategoryRepository
import kr.co.lunatalk.domain.product.repository.ProductRepository
import kr.co.lunatalk.domain.product.service.ProductService
import kr.co.lunatalk.global.exception.CustomException
import kr.co.lunatalk.global.exception.ErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CategoryService(
    private val categoryRepository: CategoryRepository,
    private val productRepository: ProductRepository,
    private val productService: ProductService
) {

    fun create(request: CategoryCreateRequest): CategoryCreateResponse {
        val isExists = existsByName(request.name)
        if (isExists) {
            throw CustomException(ErrorCode.CATEGORY_EXISTS)
        }

        val category = Category.createCategory(request.name, request.visibility)
        categoryRepository.save(category)

        return CategoryCreateResponse.of(category)
    }

    fun update(categoryId: Long, request: CategoryUpdateRequest) {
        val findCategory = findById(categoryId)

        findCategory.updateName(request.name)
        findCategory.updateVisibility(request.visibility)
    }

    fun delete(categoryId: Long) {
        val findCategory = findById(categoryId)

        findCategory.deleteStatus()
        findCategory.updateVisibility(CategoryVisibility.HIDDEN)
    }

    @Transactional(readOnly = true)
    fun getCategory(categoryId: Long): CategoryProductResponse {
        val withProducts = categoryRepository.findWithProducts(categoryId)
            .orElseThrow { CustomException(ErrorCode.CATEGORY_NOT_FOUND) }

        val productIds = withProducts.products.map { it.id!! }

        val allProducts = productService.findAllProducts(productIds)

        return CategoryProductResponse.of(withProducts.id, withProducts.name, allProducts)
    }

    @Transactional(readOnly = true)
    fun getCategoryList(): List<CategoryResponse> {
        val activeCategories = categoryRepository.findAllByStatus(CategoryStatus.ACTIVE)

        return activeCategories.mapNotNull { CategoryResponse.from(it) }
    }

    @Transactional(readOnly = true)
    fun getOneCategory(categoryId: Long): CategoryResponse {
        val category = categoryRepository.findByIdAndStatus(categoryId, CategoryStatus.ACTIVE)
            .orElseThrow { CustomException(ErrorCode.CATEGORY_NOT_FOUND) }

        return CategoryResponse.from(category)!!
    }

    @Transactional(readOnly = true)
    fun getCategoryProducts(): List<CategoryProductResponse> {
        val allWithProducts = categoryRepository.findAllWithProducts()

        return allWithProducts.map { category ->
            val productIds = category.products.map { it.id!! }
            val allProducts = productService.findAllProducts(productIds)
            CategoryProductResponse.of(category.id, category.name, allProducts)
        }
    }

    fun addProduct(categoryId: Long, request: CategoryAddProductRequest): CategoryAddProductResponse {
        val findCategory = categoryRepository.findWithProducts(categoryId)
            .orElseThrow { CustomException(ErrorCode.CATEGORY_NOT_FOUND) }

        // 기존 연관 끊기
        productRepository.bulkClearCategory(findCategory.id!!)

        // 전달 받은 상품 조회 IN
        val products = productRepository.findAllProductsByProductIds(request.productIds)

        // 연결
        // TODO: 현재 update가 단건으로 날아가는데.. 한 쿼리로 바꿔야함.
        products.forEach { findCategory.addProduct(it) }

        return CategoryAddProductResponse.of(findCategory, products)
    }

    @Transactional(readOnly = true)
    fun existsByName(name: String): Boolean {
        return categoryRepository.existsByName(name)
    }

    @Transactional(readOnly = true)
    fun findById(id: Long): Category {
        return categoryRepository.findById(id)
            .orElseThrow { CustomException(ErrorCode.CATEGORY_NOT_FOUND) }
    }
}
