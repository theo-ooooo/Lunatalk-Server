package kr.co.lunatalk.domain.category.service

import kr.co.lunatalk.domain.category.domain.Category
import kr.co.lunatalk.domain.category.domain.CategoryStatus
import kr.co.lunatalk.domain.category.domain.CategoryVisibility
import kr.co.lunatalk.domain.category.dto.request.CategoryAddProductRequest
import kr.co.lunatalk.domain.category.dto.request.CategoryCreateRequest
import kr.co.lunatalk.domain.category.dto.request.CategoryUpdateRequest
import kr.co.lunatalk.domain.category.repository.CategoryRepository
import kr.co.lunatalk.domain.product.domain.Product
import kr.co.lunatalk.domain.product.domain.ProductStatus
import kr.co.lunatalk.domain.product.domain.ProductVisibility
import kr.co.lunatalk.domain.product.repository.ProductRepository
import kr.co.lunatalk.global.exception.CustomException
import kr.co.lunatalk.global.exception.ErrorCode
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.*
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

@ExtendWith(MockitoExtension::class)
class CategoryServiceTest {

    @Mock
    private lateinit var categoryRepository: CategoryRepository

    @Mock
    private lateinit var productRepository: ProductRepository

    @InjectMocks
    private lateinit var categoryService: CategoryService

    private lateinit var testCategory: Category

    @BeforeEach
    fun setUp() {
        testCategory = Category.createCategory("테스트", CategoryVisibility.VISIBLE)
    }

    @Test
    fun `카테고리를 생성`() {
        // given
        val request = CategoryCreateRequest("테스트", CategoryVisibility.VISIBLE)
        given(categoryRepository.existsByName(anyString())).willReturn(false)

        // when
        val response = categoryService.create(request)

        // then
        assertThat(response).isNotNull()
        verify(categoryRepository).save(any(Category::class.java))
    }

    @Test
    fun `이미 존재하는 카테고리 명으로 생성`() {
        // given
        val request = CategoryCreateRequest("테스트", CategoryVisibility.VISIBLE)
        given(categoryRepository.existsByName(anyString())).willReturn(true)

        // when, then
        assertThatThrownBy { categoryService.create(request) }
            .isInstanceOf(CustomException::class.java)
            .hasMessage(ErrorCode.CATEGORY_EXISTS.message)
    }

    @Test
    fun `카테고리에 상품 추가`() {
        // given
        val productIds = listOf(1L, 2L, 3L)
        val request = CategoryAddProductRequest(productIds)

        val product1 = Product.createProduct("P1", 1000L, 10, ProductStatus.ACTIVE, ProductVisibility.VISIBLE)
        val product2 = Product.createProduct("P2", 2000L, 5, ProductStatus.ACTIVE, ProductVisibility.VISIBLE)
        val product3 = Product.createProduct("P3", 3000L, 7, ProductStatus.ACTIVE, ProductVisibility.VISIBLE)
        val mockProducts = listOf(product1, product2, product3)

        given(categoryRepository.findWithProducts(anyLong())).willReturn(Optional.of(testCategory))
        given(productRepository.findAllProductsByProductIds(productIds)).willReturn(mockProducts)

        // when
        val response = categoryService.addProduct(1L, request)

        // then
        assertThat(testCategory.products).containsExactly(product1, product2, product3)
        assertThat(response.productIds().size).isEqualTo(3)
        verify(productRepository).findAllProductsByProductIds(productIds)
    }

    @Test
    fun `없는 카테고리에 상품 추가`() {
        val productIds = listOf(1L, 2L, 3L)
        val request = CategoryAddProductRequest(productIds)

        given(categoryRepository.findWithProducts(anyLong())).willReturn(Optional.empty())

        // when then
        assertThatThrownBy { categoryService.addProduct(1L, request) }
            .isInstanceOf(CustomException::class.java)
            .hasMessage(ErrorCode.CATEGORY_NOT_FOUND.message)
    }

    @Test
    fun `상품 업데이트`() {
        // given
        val request = CategoryUpdateRequest("변경", CategoryVisibility.HIDDEN)
        given(categoryRepository.findById(anyLong())).willReturn(Optional.of(testCategory))

        // when
        categoryService.update(1L, request)

        // then
        assertThat(testCategory.name).isEqualTo("변경")
        assertThat(testCategory.visibility).isEqualTo(CategoryVisibility.HIDDEN)
    }

    @Test
    fun `상품 삭제`() {
        // given
        given(categoryRepository.findById(anyLong())).willReturn(Optional.of(testCategory))

        // when
        categoryService.delete(1L)

        // then
        assertThat(testCategory.visibility).isEqualTo(CategoryVisibility.HIDDEN)
        assertThat(testCategory.status).isEqualTo(CategoryStatus.DELETED)
    }
}
