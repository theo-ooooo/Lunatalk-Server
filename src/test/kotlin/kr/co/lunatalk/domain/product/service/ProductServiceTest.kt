package kr.co.lunatalk.domain.product.service

import kr.co.lunatalk.domain.category.domain.Category
import kr.co.lunatalk.domain.category.domain.CategoryStatus
import kr.co.lunatalk.domain.category.domain.CategoryVisibility
import kr.co.lunatalk.domain.category.repository.CategoryRepository
import kr.co.lunatalk.domain.image.domain.Image
import kr.co.lunatalk.domain.image.repository.ImageRepository
import kr.co.lunatalk.domain.product.domain.Product
import kr.co.lunatalk.domain.product.domain.ProductColor
import kr.co.lunatalk.domain.product.domain.ProductStatus
import kr.co.lunatalk.domain.product.domain.ProductVisibility
import kr.co.lunatalk.domain.product.dto.ProductWithImagesResult
import kr.co.lunatalk.domain.product.dto.request.ProductCreateRequest
import kr.co.lunatalk.domain.product.dto.request.ProductUpdateRequest
import kr.co.lunatalk.domain.product.repository.ProductRepository
import kr.co.lunatalk.domain.productlike.service.ProductLikeService
import kr.co.lunatalk.global.exception.CustomException
import kr.co.lunatalk.global.exception.ErrorCode
import kr.co.lunatalk.global.util.ProductUtil
import kr.co.lunatalk.global.util.SecurityUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.test.util.ReflectionTestUtils
import java.util.*

@ExtendWith(MockitoExtension::class)
class ProductServiceTest {

    private lateinit var productService: ProductService

    @Mock
    private lateinit var productRepository: ProductRepository

    @Mock
    private lateinit var imageRepository: ImageRepository

    @Mock
    private lateinit var categoryRepository: CategoryRepository

    @Mock
    private lateinit var productUtil: ProductUtil

    @Mock
    private lateinit var productLikeService: ProductLikeService

    @Mock
    private lateinit var securityUtil: SecurityUtil

    private lateinit var product1: Product
    private lateinit var product2: Product
    private lateinit var category: Category

    @BeforeEach
    fun setUp() {
        productService = ProductService(productRepository, imageRepository, categoryRepository, productUtil, productLikeService, securityUtil)

        product1 = Product.createProduct("상품1", 10000L, 10, ProductStatus.ACTIVE, ProductVisibility.VISIBLE)
        val color1 = ProductColor.createProductColor(product1, "red")
        product1.addProductColor(color1)
        ReflectionTestUtils.setField(product1, "id", 1L)

        product2 = Product.createProduct("상품2", 20000L, 20, ProductStatus.ACTIVE, ProductVisibility.VISIBLE)
        val color2 = ProductColor.createProductColor(product2, "blue")
        product2.addProductColor(color2)
        ReflectionTestUtils.setField(product2, "id", 2L)

        category = Category.createCategory("테스트카테고리", CategoryVisibility.VISIBLE)
        ReflectionTestUtils.setField(category, "id", 100L)
    }

    @Test
    @DisplayName("상품 상세 조회 시 좋아요 정보가 포함된다")
    fun `상품 상세 조회 좋아요 정보 포함 테스트`() {
        // given
        val productId = product1.id
        val images = listOf<Image>()
        val likeCount = 1L
        val isLiked = true

        whenever(productUtil.findProductId(productId!!)).thenReturn(product1)
        whenever(imageRepository.fetchProductImagesByProductId(productId)).thenReturn(images)
        whenever(productLikeService.getLikeCount(productId)).thenReturn(likeCount)
        whenever(securityUtil.getCurrentMemberId()).thenReturn(1L)
        whenever(productLikeService.isLiked(productId, 1L)).thenReturn(isLiked)

        // when
        val response = productService.findProductOne(productId)

        // then
        assertThat(response).isNotNull()
        assertThat(response.productId).isEqualTo(productId)
        assertThat(response.likeCount).isEqualTo(likeCount)
        assertThat(response.isLiked).isTrue()
    }

    @Test
    @DisplayName("좋아요를 누르지 않은 상품은 isLiked가 false이다")
    fun `좋아요 없는 상품 조회 테스트`() {
        // given
        val productId = product1.id
        val images = listOf<Image>()
        val likeCount = 0L
        val isLiked = false

        whenever(productUtil.findProductId(productId!!)).thenReturn(product1)
        whenever(imageRepository.fetchProductImagesByProductId(productId)).thenReturn(images)
        whenever(productLikeService.getLikeCount(productId)).thenReturn(likeCount)
        whenever(securityUtil.getCurrentMemberId()).thenReturn(1L)
        whenever(productLikeService.isLiked(productId, 1L)).thenReturn(isLiked)

        // when
        val response = productService.findProductOne(productId)

        // then
        assertThat(response).isNotNull()
        assertThat(response.likeCount).isEqualTo(0L)
        assertThat(response.isLiked).isFalse()
    }

    @Test
    @DisplayName("상품 목록 조회 시 각 상품의 좋아요 정보가 포함된다")
    fun `상품 목록 조회 좋아요 정보 포함 테스트`() {
        // given
        val products = listOf(product1, product2)
        val productPage = PageImpl(products, PageRequest.of(0, 10), 2)
        val images = listOf<Image>()
        val likeCountMap = mapOf(product1.id!! to 1L, product2.id!! to 0L)
        val likedStatusMap = mapOf(product1.id!! to true, product2.id!! to false)

        whenever(productRepository.findAll(isNull(), any<Pageable>())).thenReturn(productPage)
        whenever(imageRepository.fetchProductImagesByProductIds(any())).thenReturn(images)
        whenever(productLikeService.getLikeCounts(any())).thenReturn(likeCountMap)
        whenever(securityUtil.getCurrentMemberId()).thenReturn(1L)
        whenever(productLikeService.getLikedStatus(any(), eq(1L))).thenReturn(likedStatusMap)

        // when
        val result = productService.findAll(null, PageRequest.of(0, 10))

        // then
        assertThat(result).isNotNull()
        assertThat(result.totalElements).isEqualTo(2)

        val product1Response = result.content.firstOrNull { it.productId == product1.id }
        val product2Response = result.content.firstOrNull { it.productId == product2.id }

        assertThat(product1Response).isNotNull()
        assertThat(product1Response!!.likeCount).isEqualTo(1L)
        assertThat(product1Response.isLiked).isTrue()

        assertThat(product2Response).isNotNull()
        assertThat(product2Response!!.likeCount).isEqualTo(0L)
        assertThat(product2Response.isLiked).isFalse()
    }

    @Test
    @DisplayName("상품을 생성할 수 있다")
    fun `상품 생성 테스트`() {
        // given
        val request = ProductCreateRequest(
            "새 상품",
            15000L,
            30,
            ProductVisibility.VISIBLE,
            listOf("red", "blue"),
            category.id!!
        )

        whenever(categoryRepository.findByIdAndStatus(category.id!!, CategoryStatus.ACTIVE))
            .thenReturn(Optional.of(category))
        whenever(productRepository.save(any<Product>()))
            .thenAnswer { invocation ->
                val saved = invocation.getArgument<Product>(0)
                ReflectionTestUtils.setField(saved, "id", 3L)
                saved
            }

        // when
        val savedProduct = productService.save(request)

        // then
        assertThat(savedProduct.id).isNotNull()
        assertThat(savedProduct.name).isEqualTo("새 상품")
        assertThat(savedProduct.price).isEqualTo(15000L)
        assertThat(savedProduct.quantity).isEqualTo(30)
        assertThat(savedProduct.visibility).isEqualTo(ProductVisibility.VISIBLE)
        assertThat(savedProduct.productColor).hasSize(2)
        assertThat(savedProduct.category!!.id).isEqualTo(category.id)
    }

    @Test
    @DisplayName("존재하지 않는 카테고리로 상품 생성 시 예외가 발생한다")
    fun `존재하지 않는 카테고리로 상품 생성 실패 테스트`() {
        // given
        val request = ProductCreateRequest(
            "새 상품",
            15000L,
            30,
            ProductVisibility.VISIBLE,
            listOf("red"),
            -1L
        )

        whenever(categoryRepository.findByIdAndStatus(-1L, CategoryStatus.ACTIVE))
            .thenReturn(Optional.empty())

        // when & then
        assertThrows(CustomException::class.java) { productService.save(request) }
    }

    @Test
    @DisplayName("상품 정보를 수정할 수 있다")
    fun `상품 수정 테스트`() {
        // given
        val request = ProductUpdateRequest(
            "수정된 상품명",
            20000L,
            50,
            ProductVisibility.HIDDEN,
            listOf("green", "yellow"),
            category.id
        )

        whenever(productUtil.findProductId(product1.id!!)).thenReturn(product1)
        whenever(categoryRepository.findByIdAndStatus(category.id!!, CategoryStatus.ACTIVE))
            .thenReturn(Optional.of(category))

        // when
        productService.update(product1.id!!, request)

        // then
        assertThat(product1.name).isEqualTo("수정된 상품명")
        assertThat(product1.price).isEqualTo(20000L)
        assertThat(product1.quantity).isEqualTo(50)
        assertThat(product1.visibility).isEqualTo(ProductVisibility.HIDDEN)
    }

    @Test
    @DisplayName("상품을 삭제하면 상태가 DELETED로 변경되고 visibility가 HIDDEN으로 변경된다")
    fun `상품 삭제 테스트`() {
        // given
        val productId = product1.id!!

        whenever(productUtil.findProductId(productId)).thenReturn(product1)

        // when
        productService.delete(productId)

        // then
        assertThat(product1.status).isEqualTo(ProductStatus.DELETED)
        assertThat(product1.visibility).isEqualTo(ProductVisibility.HIDDEN)
    }

    @Test
    @DisplayName("존재하지 않는 상품 조회 시 예외가 발생한다")
    fun `존재하지 않는 상품 조회 실패 테스트`() {
        // given
        val nonExistentProductId = -1L

        whenever(productUtil.findProductId(nonExistentProductId))
            .thenThrow(CustomException(ErrorCode.PRODUCT_NOT_FOUND))

        // when & then
        assertThrows(CustomException::class.java) { productService.findProductOne(nonExistentProductId) }
    }

    @Test
    @DisplayName("여러 상품을 ID 리스트로 조회할 수 있다")
    fun `여러 상품 조회 테스트`() {
        // given
        val productIds = listOf(product1.id!!, product2.id!!)
        val products = listOf(product1, product2)
        val imageMap = mapOf(product1.id!! to listOf<Image>(), product2.id!! to listOf<Image>())
        val likeCountMap = mapOf(product1.id!! to 1L, product2.id!! to 0L)
        val likedStatusMap = mapOf(product1.id!! to true, product2.id!! to false)

        whenever(productUtil.findAllProducts(productIds))
            .thenReturn(ProductWithImagesResult(products, imageMap))
        whenever(productLikeService.getLikeCounts(productIds)).thenReturn(likeCountMap)
        whenever(securityUtil.getCurrentMemberId()).thenReturn(1L)
        whenever(productLikeService.getLikedStatus(productIds, 1L)).thenReturn(likedStatusMap)

        // when
        val responses = productService.findAllProducts(productIds)

        // then
        assertThat(responses).hasSize(2)
        val product1Response = responses.first { it.productId == product1.id }
        val product2Response = responses.first { it.productId == product2.id }

        assertThat(product1Response.likeCount).isEqualTo(1L)
        assertThat(product1Response.isLiked).isTrue()
        assertThat(product2Response.likeCount).isEqualTo(0L)
        assertThat(product2Response.isLiked).isFalse()
    }

    @Test
    @DisplayName("상품명으로 검색할 수 있다")
    fun `상품명 검색 테스트`() {
        // given
        val product3 = Product.createProduct("검색테스트상품", 30000L, 30, ProductStatus.ACTIVE, ProductVisibility.VISIBLE)
        val color3 = ProductColor.createProductColor(product3, "green")
        product3.addProductColor(color3)
        ReflectionTestUtils.setField(product3, "id", 3L)

        val products = listOf(product3)
        val productPage = PageImpl(products, PageRequest.of(0, 10), 1)
        val images = listOf<Image>()
        val likeCountMap = mapOf(product3.id!! to 0L)
        val likedStatusMap = mapOf(product3.id!! to false)

        whenever(productRepository.findAll(eq("검색테스트"), any<Pageable>())).thenReturn(productPage)
        whenever(imageRepository.fetchProductImagesByProductIds(any())).thenReturn(images)
        whenever(productLikeService.getLikeCounts(any())).thenReturn(likeCountMap)
        whenever(securityUtil.getCurrentMemberId()).thenReturn(1L)
        whenever(productLikeService.getLikedStatus(any(), eq(1L))).thenReturn(likedStatusMap)

        // when
        val result = productService.findAll("검색테스트", PageRequest.of(0, 10))

        // then
        assertThat(result).isNotNull()
        assertThat(result.totalElements).isEqualTo(1)
        assertThat(result.content.any { it.name!!.contains("검색테스트") }).isTrue()
    }

    @Test
    @DisplayName("상품명이 null이면 전체 상품을 조회한다")
    fun `전체 상품 조회 테스트`() {
        // given
        val products = listOf(product1, product2)
        val productPage = PageImpl(products, PageRequest.of(0, 10), 2)
        val images = listOf<Image>()
        val likeCountMap = mapOf(product1.id!! to 0L, product2.id!! to 0L)
        val likedStatusMap = mapOf(product1.id!! to false, product2.id!! to false)

        whenever(productRepository.findAll(isNull(), any<Pageable>())).thenReturn(productPage)
        whenever(imageRepository.fetchProductImagesByProductIds(any())).thenReturn(images)
        whenever(productLikeService.getLikeCounts(any())).thenReturn(likeCountMap)
        whenever(securityUtil.getCurrentMemberId()).thenReturn(1L)
        whenever(productLikeService.getLikedStatus(any(), eq(1L))).thenReturn(likedStatusMap)

        // when
        val result = productService.findAll(null, PageRequest.of(0, 10))

        // then
        assertThat(result).isNotNull()
        assertThat(result.totalElements).isEqualTo(2)
    }
}
