package kr.co.lunatalk.domain.cartitem.service

import kr.co.lunatalk.domain.cartitem.domain.CartItem
import kr.co.lunatalk.domain.cartitem.dto.request.CreateCartItemRequest
import kr.co.lunatalk.domain.cartitem.dto.request.UpdateCartItemRequest
import kr.co.lunatalk.domain.cartitem.repository.CartItemRepository
import kr.co.lunatalk.domain.image.domain.Image
import kr.co.lunatalk.domain.member.domain.Member
import kr.co.lunatalk.domain.member.domain.Profile
import kr.co.lunatalk.domain.product.domain.Product
import kr.co.lunatalk.domain.product.domain.ProductStatus
import kr.co.lunatalk.domain.product.domain.ProductVisibility
import kr.co.lunatalk.domain.product.dto.ProductWithImagesResult
import kr.co.lunatalk.domain.productlike.service.ProductLikeService
import kr.co.lunatalk.global.exception.CustomException
import kr.co.lunatalk.global.util.MemberUtil
import kr.co.lunatalk.global.util.ProductUtil
import kr.co.lunatalk.global.util.SecurityUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.util.ReflectionTestUtils
import java.util.*

@ExtendWith(MockitoExtension::class)
class CartItemServiceTest {

    @InjectMocks
    private lateinit var cartItemService: CartItemService

    @Mock
    private lateinit var cartItemRepository: CartItemRepository

    @Mock
    private lateinit var memberUtil: MemberUtil

    @Mock
    private lateinit var productUtil: ProductUtil

    @Mock
    private lateinit var productLikeService: ProductLikeService

    @Mock
    private lateinit var securityUtil: SecurityUtil

    private lateinit var member: Member
    private lateinit var product: Product

    @BeforeEach
    fun setUp() {
        member = Member.createMember("TEST", "1234", Profile.of("TEST", ""), "01012341234", "kkwondev@gmail.com")
        ReflectionTestUtils.setField(member, "id", 1L)

        product = Product.createProduct("TEST_PRODUCT", 10000L, 100, ProductStatus.ACTIVE, ProductVisibility.VISIBLE)
        ReflectionTestUtils.setField(product, "id", 100L)
    }

    @Test
    fun `create shouldCreateCartItemSuccessfully`() {
        // given
        val request = CreateCartItemRequest(product.id!!, 2)

        `when`(memberUtil.currentMember).thenReturn(member)
        `when`(productUtil.findProductId(product.id!!)).thenReturn(product)
        `when`(cartItemRepository.save(any(CartItem::class.java)))
            .thenAnswer { invocation ->
                val saved = invocation.getArgument<CartItem>(0)
                ReflectionTestUtils.setField(saved, "id", 1L)
                saved
            }

        // when
        val response = cartItemService.create(request)

        // then
        assertThat(response.cartItemId).isEqualTo(1L)
    }

    @Test
    fun `findAll shouldReturnCartItems`() {
        // given
        val cartItem = CartItem.createCartItem(member, product, 3)
        ReflectionTestUtils.setField(cartItem, "id", 1L)

        val cartItems = listOf(cartItem)
        val imageMap = mapOf<Long, List<Image>>(product.id!! to listOf())

        `when`(memberUtil.currentMember).thenReturn(member)
        `when`(cartItemRepository.findByMemberId(member.id!!)).thenReturn(cartItems)
        `when`(productUtil.findAllProducts(listOf(product.id!!)))
            .thenReturn(ProductWithImagesResult(listOf(product), imageMap))
        `when`(productLikeService.getLikeCounts(listOf(product.id!!)))
            .thenReturn(mapOf(product.id!! to 0L))
        `when`(securityUtil.getCurrentMemberId()).thenReturn(member.id!!)
        `when`(productLikeService.getLikedStatus(listOf(product.id!!), member.id!!))
            .thenReturn(mapOf(product.id!! to false))

        // when
        val result = cartItemService.findAll()

        // then
        assertThat(result).hasSize(1)
        assertThat(result[0].quantity).isEqualTo(3)
    }

    @Test
    fun `deleteById shouldDeleteCartItem`() {
        // given
        val cartItem = CartItem.createCartItem(member, product, 1)
        ReflectionTestUtils.setField(cartItem, "id", 10L)

        `when`(memberUtil.currentMember).thenReturn(member)
        `when`(cartItemRepository.findById(10L)).thenReturn(Optional.of(cartItem))

        // when
        cartItemService.deleteById(10L)

        // then
        verify(cartItemRepository).deleteById(10L)
    }

    @Test
    fun `updateById shouldUpdateQuantity`() {
        // given
        val cartItem = spy(CartItem.createCartItem(member, product, 1))
        ReflectionTestUtils.setField(cartItem, "id", 5L)

        `when`(memberUtil.currentMember).thenReturn(member)
        `when`(cartItemRepository.findById(5L)).thenReturn(Optional.of(cartItem))

        // when
        cartItemService.updateById(5L, UpdateCartItemRequest(10))

        // then
        verify(cartItem).updateQuantity(10)
    }

    @Test
    fun `deleteById shouldThrow ifUserMismatch`() {
        // given
        val another = Member.createMember("TEST2", "1234", Profile.of("TEST", ""), "01012341234", "kkwondev@gmail.com")
        ReflectionTestUtils.setField(another, "id", 999L)

        val cartItem = CartItem.createCartItem(another, product, 1)
        ReflectionTestUtils.setField(cartItem, "id", 20L)

        `when`(memberUtil.currentMember).thenReturn(member)
        `when`(cartItemRepository.findById(20L)).thenReturn(Optional.of(cartItem))

        // expect
        assertThrows(CustomException::class.java) { cartItemService.deleteById(20L) }
    }

    @Test
    fun `deleteById shouldThrow ifCartItemNotFound`() {
        // given
        `when`(memberUtil.currentMember).thenReturn(member)
        `when`(cartItemRepository.findById(99L)).thenReturn(Optional.empty())

        // expect
        assertThrows(CustomException::class.java) { cartItemService.deleteById(99L) }
    }
}
