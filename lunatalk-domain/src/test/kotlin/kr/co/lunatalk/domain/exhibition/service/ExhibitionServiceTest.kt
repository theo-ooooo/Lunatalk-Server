package kr.co.lunatalk.domain.exhibition.service

import kr.co.lunatalk.domain.exhibition.domain.Exhibition
import kr.co.lunatalk.domain.exhibition.domain.ExhibitionProduct
import kr.co.lunatalk.domain.exhibition.domain.ExhibitionVisibility
import kr.co.lunatalk.domain.exhibition.dto.request.ExhibitionCreateRequest
import kr.co.lunatalk.domain.exhibition.dto.request.ExhibitionUpdateRequest
import kr.co.lunatalk.domain.exhibition.repository.ExhibitionRepository
import kr.co.lunatalk.domain.product.domain.Product
import kr.co.lunatalk.domain.product.domain.ProductStatus
import kr.co.lunatalk.domain.product.domain.ProductVisibility
import kr.co.lunatalk.domain.product.dto.ProductWithImagesResult
import kr.co.lunatalk.domain.productlike.service.ProductLikeService
import kr.co.lunatalk.global.util.ProductUtil
import kr.co.lunatalk.global.util.SecurityUtil
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.test.util.ReflectionTestUtils
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class ExhibitionServiceTest {

    private lateinit var exhibitionService: ExhibitionService

    @Mock
    private lateinit var exhibitionRepository: ExhibitionRepository

    @Mock
    private lateinit var productUtil: ProductUtil

    @Mock
    private lateinit var productLikeService: ProductLikeService

    @Mock
    private lateinit var securityUtil: SecurityUtil

    @BeforeEach
    fun setUp() {
        exhibitionService = ExhibitionService(exhibitionRepository, productUtil, productLikeService, securityUtil)
    }

    @Test
    fun `createExhibition 정상생성`() {
        // given
        val productIds = listOf(1L, 2L)
        val request = ExhibitionCreateRequest(
            "기획전 제목", "기획전 설명", ExhibitionVisibility.VISIBLE,
            productIds,
            LocalDateTime.now(), LocalDateTime.now().plusDays(7)
        )

        val mockProducts = productIds.map { id ->
            Product.createProduct("상품$id", 10000L, 10, ProductStatus.ACTIVE, ProductVisibility.VISIBLE)
        }

        whenever(productUtil.findAllProductByProductIdIn(productIds)).thenReturn(mockProducts)

        // when
        val response = exhibitionService.createExhibition(request)

        // then
        assertNotNull(response)
        verify(exhibitionRepository).save(any<Exhibition>())
    }

    @Test
    fun `getAllExhibitions 정상조회`() {
        // given
        val exhibition = Exhibition.createExhibition(
            "기획전", "설명", ExhibitionVisibility.VISIBLE,
            LocalDateTime.now(), LocalDateTime.now().plusDays(3)
        )

        val product = Product.createProduct("상품1", 10000L, 10, ProductStatus.ACTIVE, ProductVisibility.VISIBLE)
        ReflectionTestUtils.setField(product, "id", 1L)

        val ep = ExhibitionProduct.createExhibitionProduct(exhibition, product, 1)
        exhibition.addProducts(listOf(ep))

        whenever(exhibitionRepository.findAll()).thenReturn(listOf(exhibition))
        whenever(productUtil.findAllProducts(any())).thenReturn(
            ProductWithImagesResult(
                listOf(product),
                mapOf(1L to listOf())
            )
        )
        whenever(productLikeService.getLikeCounts(any())).thenReturn(mapOf(1L to 0L))
        whenever(securityUtil.getCurrentMemberId()).thenReturn(1L)
        whenever(productLikeService.getLikedStatus(any(), any())).thenReturn(mapOf(1L to false))

        // when
        val result = exhibitionService.getAllExhibitions()

        // then
        assertEquals(1, result.size)
        verify(productUtil).findAllProducts(listOf(1L))
    }

    @Test
    fun `updateExhibition 정상수정`() {
        // given
        val exhibition = Exhibition.createExhibition(
            "기존 제목", "기존 설명", ExhibitionVisibility.HIDDEN,
            LocalDateTime.now(), LocalDateTime.now().plusDays(3)
        )
        ReflectionTestUtils.setField(exhibition, "id", 1L)

        val request = ExhibitionUpdateRequest(
            "수정 제목", "수정 설명", ExhibitionVisibility.VISIBLE,
            listOf(1L, 2L),
            LocalDateTime.now(), LocalDateTime.now().plusDays(10)
        )

        whenever(exhibitionRepository.findById(1L)).thenReturn(Optional.of(exhibition))
        whenever(productUtil.findAllProductByProductIdIn(any())).thenReturn(
            listOf(
                Product.createProduct("상품1", 10000L, 10, ProductStatus.ACTIVE, ProductVisibility.VISIBLE),
                Product.createProduct("상품1", 10000L, 10, ProductStatus.ACTIVE, ProductVisibility.VISIBLE)
            )
        )

        // when
        exhibitionService.updateExhibition(1L, request)

        // then
        assertEquals("수정 제목", exhibition.title)
        verify(exhibitionRepository).deleteProductByExhibitionId(1L)
    }

    @Test
    fun `deleteExhibition 정상삭제`() {
        // given
        val exhibition = Exhibition.createExhibition(
            "삭제 기획전", "설명", ExhibitionVisibility.HIDDEN,
            LocalDateTime.now(), LocalDateTime.now().plusDays(5)
        )
        ReflectionTestUtils.setField(exhibition, "id", 1L)

        whenever(exhibitionRepository.findById(1L)).thenReturn(Optional.of(exhibition))

        // when
        exhibitionService.deleteExhibition(1L)

        // then
        verify(exhibitionRepository).deleteById(1L)
    }
}
