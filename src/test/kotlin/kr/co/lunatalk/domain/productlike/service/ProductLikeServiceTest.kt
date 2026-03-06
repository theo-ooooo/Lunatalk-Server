package kr.co.lunatalk.domain.productlike.service

import kr.co.lunatalk.domain.member.domain.Member
import kr.co.lunatalk.domain.member.domain.Profile
import kr.co.lunatalk.domain.product.domain.Product
import kr.co.lunatalk.domain.product.domain.ProductColor
import kr.co.lunatalk.domain.product.domain.ProductStatus
import kr.co.lunatalk.domain.product.domain.ProductVisibility
import kr.co.lunatalk.domain.product.dto.ProductWithImagesResult
import kr.co.lunatalk.domain.productlike.domain.ProductLike
import kr.co.lunatalk.domain.productlike.repository.ProductLikeRepository
import kr.co.lunatalk.global.util.MemberUtil
import kr.co.lunatalk.global.util.ProductUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
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
class ProductLikeServiceTest {

    private lateinit var productLikeService: ProductLikeService

    @Mock
    private lateinit var productLikeRepository: ProductLikeRepository

    @Mock
    private lateinit var memberUtil: MemberUtil

    @Mock
    private lateinit var productUtil: ProductUtil

    private lateinit var member1: Member
    private lateinit var member2: Member
    private lateinit var product1: Product
    private lateinit var product2: Product

    @BeforeEach
    fun setUp() {
        productLikeService = ProductLikeService(productLikeRepository, memberUtil, productUtil)

        member1 = Member.createMember(
            "testuser1",
            "1234",
            Profile.of("테스트유저1", "img1"),
            "01012341234",
            "test1@test.com"
        )
        ReflectionTestUtils.setField(member1, "id", 1L)

        member2 = Member.createMember(
            "testuser2",
            "1234",
            Profile.of("테스트유저2", "img2"),
            "01012345678",
            "test2@test.com"
        )
        ReflectionTestUtils.setField(member2, "id", 2L)

        product1 = Product.createProduct("상품1", 10000L, 10, ProductStatus.ACTIVE, ProductVisibility.VISIBLE)
        val color1 = ProductColor.createProductColor(product1, "red")
        product1.addProductColor(color1)
        ReflectionTestUtils.setField(product1, "id", 100L)

        product2 = Product.createProduct("상품2", 20000L, 20, ProductStatus.ACTIVE, ProductVisibility.VISIBLE)
        val color2 = ProductColor.createProductColor(product2, "blue")
        product2.addProductColor(color2)
        ReflectionTestUtils.setField(product2, "id", 200L)
    }

    @Test
    @DisplayName("좋아요를 누르면 ProductLike가 생성된다")
    fun `좋아요 추가 테스트`() {
        // given
        val productId = product1.id!!

        whenever(memberUtil.currentMember).thenReturn(member1)
        whenever(productUtil.findProductId(productId)).thenReturn(product1)
        whenever(productLikeRepository.findByMemberIdAndProductId(member1.id!!, productId))
            .thenReturn(Optional.empty())
        whenever(productLikeRepository.save(any<ProductLike>()))
            .thenAnswer { invocation ->
                val saved = invocation.getArgument<ProductLike>(0)
                ReflectionTestUtils.setField(saved, "id", 1L)
                saved
            }

        // when
        productLikeService.toggleLike(productId)

        // then
        verify(productLikeRepository).save(any<ProductLike>())
        verify(productLikeRepository, never()).delete(any<ProductLike>())
    }

    @Test
    @DisplayName("이미 좋아요를 누른 상태에서 다시 누르면 좋아요가 취소된다")
    fun `좋아요 취소 테스트`() {
        // given
        val productId = product1.id!!
        val existingLike = ProductLike.create(member1, product1)
        ReflectionTestUtils.setField(existingLike, "id", 1L)

        whenever(memberUtil.currentMember).thenReturn(member1)
        whenever(productUtil.findProductId(productId)).thenReturn(product1)
        whenever(productLikeRepository.findByMemberIdAndProductId(member1.id!!, productId))
            .thenReturn(Optional.of(existingLike))

        // when
        productLikeService.toggleLike(productId)

        // then
        verify(productLikeRepository).delete(existingLike)
        verify(productLikeRepository, never()).save(any<ProductLike>())
    }

    @Test
    @DisplayName("상품의 좋아요 개수를 조회할 수 있다")
    fun `좋아요 개수 조회 테스트`() {
        // given
        val productId = product1.id!!
        whenever(productLikeRepository.countByProductId(productId)).thenReturn(2L)

        // when
        val likeCount = productLikeService.getLikeCount(productId)

        // then
        assertThat(likeCount).isEqualTo(2L)
    }

    @Test
    @DisplayName("여러 상품의 좋아요 개수를 일괄 조회할 수 있다")
    fun `여러 상품 좋아요 개수 조회 테스트`() {
        // given
        val productIds = listOf(product1.id!!, product2.id!!)
        val expectedCounts = mapOf(
            product1.id!! to 2L,
            product2.id!! to 1L
        )
        whenever(productLikeRepository.countByProductIds(productIds)).thenReturn(expectedCounts)

        // when
        val likeCounts = productLikeService.getLikeCounts(productIds)

        // then
        assertThat(likeCounts).hasSize(2)
        assertThat(likeCounts[product1.id]).isEqualTo(2L)
        assertThat(likeCounts[product2.id]).isEqualTo(1L)
    }

    @Test
    @DisplayName("특정 회원이 상품에 좋아요를 눌렀는지 확인할 수 있다")
    fun `좋아요 여부 확인 테스트`() {
        // given
        val productId = product1.id!!
        val existingLike = ProductLike.create(member1, product1)
        ReflectionTestUtils.setField(existingLike, "id", 1L)

        whenever(productLikeRepository.findByMemberIdAndProductId(member1.id!!, productId))
            .thenReturn(Optional.of(existingLike))
        whenever(productLikeRepository.findByMemberIdAndProductId(member2.id!!, productId))
            .thenReturn(Optional.empty())

        // when
        val isLikedByMember1 = productLikeService.isLiked(productId, member1.id!!)
        val isLikedByMember2 = productLikeService.isLiked(productId, member2.id!!)

        // then
        assertTrue(isLikedByMember1)
        assertFalse(isLikedByMember2)
    }

    @Test
    @DisplayName("여러 상품에 대한 좋아요 여부를 일괄 확인할 수 있다")
    fun `여러 상품 좋아요 여부 확인 테스트`() {
        // given
        val productIds = listOf(product1.id!!, product2.id!!)
        val expectedStatus = mapOf(
            product1.id!! to true,
            product2.id!! to false
        )
        whenever(productLikeRepository.existsByMemberIdAndProductIds(member1.id!!, productIds))
            .thenReturn(expectedStatus)

        // when
        val likedStatus = productLikeService.getLikedStatus(productIds, member1.id!!)

        // then
        assertThat(likedStatus).hasSize(2)
        assertTrue(likedStatus[product1.id]!!)
        assertFalse(likedStatus[product2.id]!!)
    }

    @Test
    @DisplayName("비회원의 경우 좋아요 여부는 false를 반환한다")
    fun `비회원 좋아요 여부 테스트`() {
        // given
        val productId = product1.id

        // when
        val isLiked = productLikeService.isLiked(productId!!, null)

        // then
        assertFalse(isLiked)
        verify(productLikeRepository, never()).findByMemberIdAndProductId(any(), any())
    }

    @Test
    @DisplayName("좋아요가 없는 상품의 개수는 0을 반환한다")
    fun `좋아요 없는 상품 개수 테스트`() {
        // given
        val productId = product1.id
        whenever(productLikeRepository.countByProductId(productId!!)).thenReturn(null)

        // when
        val likeCount = productLikeService.getLikeCount(productId)

        // then
        assertThat(likeCount).isEqualTo(0L)
    }

    @Test
    @DisplayName("내가 좋아요한 상품 목록을 좋아요 생성일 최신순으로 페이징 조회할 수 있다")
    fun `내 좋아요 상품 목록 조회 테스트`() {
        // given
        val pageable = PageRequest.of(0, 10)
        val likedProductIds = listOf(product2.id!!, product1.id!!) // 최신순 가정

        whenever(memberUtil.currentMember).thenReturn(member1)
        whenever(productLikeRepository.findLikedProductIdsByMemberId(eq(member1.id!!), any<Pageable>()))
            .thenReturn(PageImpl(likedProductIds, pageable, likedProductIds.size.toLong()))

        // ProductUtil은 정렬을 보장하지 않는다고 가정하고, 일부러 역순으로 반환
        whenever(productUtil.findAllProducts(likedProductIds))
            .thenReturn(ProductWithImagesResult(listOf(product1, product2), mapOf()))

        whenever(productLikeRepository.countByProductIds(likedProductIds))
            .thenReturn(mapOf(product1.id!! to 1L, product2.id!! to 5L))

        // when
        val page = productLikeService.findMyLikedProducts(pageable)

        // then
        assertThat(page.totalElements).isEqualTo(2)
        assertThat(page.content).hasSize(2)
        assertThat(page.content[0].productId).isEqualTo(product2.id)
        assertThat(page.content[0].isLiked).isTrue()
        assertThat(page.content[0].likeCount).isEqualTo(5L)

        assertThat(page.content[1].productId).isEqualTo(product1.id)
        assertThat(page.content[1].isLiked).isTrue()
        assertThat(page.content[1].likeCount).isEqualTo(1L)
    }
}
