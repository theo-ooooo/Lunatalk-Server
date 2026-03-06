package kr.co.lunatalk.domain.order.service

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import kr.co.lunatalk.domain.member.domain.Member
import kr.co.lunatalk.domain.member.domain.MemberRole
import kr.co.lunatalk.domain.member.domain.Profile
import kr.co.lunatalk.domain.member.repository.MemberRepository
import kr.co.lunatalk.domain.order.domain.OptionSnapshot
import kr.co.lunatalk.domain.order.dto.request.OrderCreateRequest
import kr.co.lunatalk.domain.order.dto.request.OrderProductRequest
import kr.co.lunatalk.domain.order.repository.OrderRepository
import kr.co.lunatalk.domain.product.domain.Product
import kr.co.lunatalk.domain.product.domain.ProductColor
import kr.co.lunatalk.domain.product.domain.ProductStatus
import kr.co.lunatalk.domain.product.domain.ProductVisibility
import kr.co.lunatalk.domain.product.repository.ProductRepository
import kr.co.lunatalk.global.exception.CustomException
import kr.co.lunatalk.global.security.PrincipalDetails
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class OrderServiceTest {

    @Autowired
    lateinit var orderService: OrderService

    @Autowired
    lateinit var memberRepository: MemberRepository

    @Autowired
    lateinit var productRepository: ProductRepository

    @Autowired
    lateinit var orderRepository: OrderRepository

    @PersistenceContext
    lateinit var em: EntityManager

    private lateinit var member: Member
    private lateinit var product: Product

    @BeforeEach
    fun setup() {
        member = Member.createMember(
            "testuser",
            "1234",
            Profile.of("테스트닉", "img"),
            "01012341234",
            "kkwondev@gmail.com"
        )
        memberRepository.save(member)

        product = Product.createProduct("테스트", 5000L, 1, ProductStatus.ACTIVE, ProductVisibility.VISIBLE)
        val productColor = ProductColor.createProductColor(product, "blue")
        product.addProductColor(productColor)
        productRepository.save(product)

        val principalDetails = PrincipalDetails(member.id, MemberRole.ADMIN)
        SecurityContextHolder.getContext().authentication =
            UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.authorities)
    }

    @Test
    fun `주문 생성 테스트`() {
        println("product = ${product.id}")
        // given
        val request = OrderCreateRequest(
            listOf(
                OrderProductRequest(product.id, 2, OptionSnapshot("blue"))
            )
        )

        // when
        val response = orderService.createOrder(request)

        // then
        assertNotNull(response.orderNumber())
        assertNotNull(response.orderId())

        val order = orderRepository.findByOrderWithItems(response.orderNumber())

        assertNotNull(order.get())
        assertEquals(1, order.get().orderItems.size)
        assertEquals(5000L * 2, order.get().totalPrice)
    }

    @Test
    fun `존재하지 않는 상품으로 주문 시 예외`() {
        // given
        val request = OrderCreateRequest(
            listOf(
                OrderProductRequest(-1L, 1, OptionSnapshot("blue"))
            )
        )

        // when & then
        assertThrows(CustomException::class.java) { orderService.createOrder(request) }
    }

    @Test
    fun `회원의 주문 목록을 페이지로 조회`() {
        println("product = ${product.id}")
        // given
        for (i in 0 until 3) {
            val request = OrderCreateRequest(
                listOf(
                    OrderProductRequest(product.id, 1, OptionSnapshot("blue"))
                )
            )
            orderService.createOrder(request)
        }

        // when
        val pageable = PageRequest.of(0, 2, Sort.by("createdAt").descending())
        val result = orderService.findOrdersByMemberId(member.id, pageable)

        // then
        assertNotNull(result)
        assertEquals(0, result.totalElements)
        assertEquals(0, result.content.size)
    }
}
